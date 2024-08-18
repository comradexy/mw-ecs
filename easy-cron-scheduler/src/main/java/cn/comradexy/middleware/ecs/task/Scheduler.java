package cn.comradexy.middleware.ecs.task;

import cn.comradexy.middleware.ecs.common.ScheduleContext;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.domain.TaskHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时任务调度器
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-22
 * @Description: 定时任务调度器
 */
public class Scheduler implements IScheduler, DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TaskScheduler taskScheduler;

    /**
     * 系统任务: 任务状态监控、任务定期存储/清理等
     */
    private final Map<String, ScheduledTask> systemTasks = new ConcurrentHashMap<>(8);

    /**
     * 已被调度的任务
     */
    private final Map<String, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(64);

    /**
     * 结束时间监控任务
     */
    private final Map<String, ScheduledTask> expireMonitors = new ConcurrentHashMap<>(64);

    private SqlSessionFactory sqlSessionFactory;

    @Nullable
    @Autowired
    public SqlSessionFactory setSqlSessionFactory() {
        // 引入依赖，避免sqlSessionFactory在Scheduler销毁之前就被销毁
        // 并且enableStorage为false时，不会注入sqlSessionFactory，所以需要@Nullable，避免报错
        return sqlSessionFactory;
    }

    public Scheduler(TaskScheduler taskScheduler) {
        Assert.notNull(taskScheduler, "TaskScheduler must not be null");
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void scheduleTask(String taskKey) {
        clearInvalidTasks();

        // 获取任务信息
        ExecDetail execDetail = ScheduleContext.taskStore.getExecDetail(taskKey);
        TaskHandler job = ScheduleContext.taskStore.getTaskHandler(execDetail.getTaskHandlerKey());

        // 检查任务状态是否为INIT
        if (!ExecDetail.ExecState.INIT.equals(execDetail.getState())) {
            logger.error("[EasyCronScheduler] Task: [key-{}] is not in INIT state, unable to schedule", taskKey);
            return;
        }

        // 启动任务
        runTask(job, execDetail);
    }

    @Override
    public void cancelTask(String taskKey) {
        // 1.删除缓存中的任务
        ScheduledTask scheduledTask = scheduledTasks.remove(taskKey);

        // 2.停止正在执行任务
        if (null != scheduledTask) {
            scheduledTask.cancel();
        }

        // 3.更新任务状态为已完成
        updateTaskSate(taskKey, ExecDetail.ExecState.COMPLETE);
    }

    @Override
    public void pauseTask(String taskKey) {
        // 1.删除缓存中的任务
        ScheduledTask scheduledTask = scheduledTasks.remove(taskKey);

        // TODO: 判断scheduleTask是否为null，并检查任务状态

        // 2.停止正在执行任务
        if (null != scheduledTask) {
            scheduledTask.cancel();
        }

        // 3.更新任务状态为暂停
        updateTaskSate(taskKey, ExecDetail.ExecState.PAUSED);
    }

    @Override
    public void resumeTask(String taskKey) {
        clearInvalidTasks();

        // 获取任务信息
        ExecDetail execDetail = ScheduleContext.taskStore.getExecDetail(taskKey);
        TaskHandler job = ScheduleContext.taskStore.getTaskHandler(execDetail.getTaskHandlerKey());

        if (null != scheduledTasks.get(taskKey)) {
            logger.error("[EasyCronScheduler] Task: [key-{}] is already running, unable to resume", taskKey);
            return;
        }

        // 检查任务状态是否为PAUSED、BLOCKED、RUNNING
        // 如果发生故障时，任务状态未及时变更为PAUSED，数据库中的状态可能为RUNNING，此时允许恢复RUNNING状态的任务
        if (!(ExecDetail.ExecState.PAUSED.equals(execDetail.getState())
                || ExecDetail.ExecState.BLOCKED.equals(execDetail.getState())
                || ExecDetail.ExecState.RUNNING.equals(execDetail.getState()))) {
            logger.error("[EasyCronScheduler] Task: [key-{}] is not in PAUSED/BLOCKED/RUNNING state, unable to resume",
                    taskKey);
            return;
        }

        // TODO: 根据任务的上次执行时间和执行次数，计算下次执行时间，
        //  改造ExecDetail，增加nextExecTime等字段

        // 启动任务
        runTask(job, execDetail);
    }

    private void clearInvalidTasks() {
        // 清空缓存中失效的任务
        scheduledTasks.forEach((key, value) -> {
            if (!ExecDetail.ExecState.RUNNING.equals(ScheduleContext.taskStore.getExecDetail(key).getState())) {
                if (!value.isCancelled()) value.cancel();
                scheduledTasks.remove(key);
            }
        });
    }

    private void runTask(TaskHandler job, ExecDetail execDetail) {
        String taskKey = execDetail.getKey();

        // 检查执行次数是否超过最大允许执行次数
        if (execDetail.getExecCount() >= execDetail.getMaxExecCount()) {
            logger.error("[EasyCronScheduler] Task: [key-{}] has reached the maximum number of executions, " +
                    "unable to start", taskKey);
            updateTaskSate(taskKey, ExecDetail.ExecState.COMPLETE);
            return;
        }

        // 设置过期监控
        if (!setExpireMonitor(taskKey, execDetail.getEndTime())) {
            logger.error("[EasyCronScheduler] Task: [key-{}] has expired, unable to start", taskKey);
            updateTaskSate(taskKey, ExecDetail.ExecState.COMPLETE);
            return;
        }

        // 组装任务
        Object bean = getBean(job.getBeanName(), job.getBeanClassName());
        if (null == bean) {
            logger.error("[EasyCronScheduler] Task: [key-{}] failed to start, unable to get bean", taskKey);
            return;
        }
        Method method = ReflectionUtils.findMethod(bean.getClass(), job.getMethodName());
        if (null == method) {
            logger.error("[EasyCronScheduler] Task: [key-{}] failed to start, unable to get method", taskKey);
            return;
        }
        Runnable runnable = () -> {
            ReflectionUtils.makeAccessible(method);
            try {
                method.invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
        SchedulingRunnable schedulingRunnable = new SchedulingRunnable(taskKey, runnable);

        // 创建定时任务
        CronTask cronTask = new CronTask(schedulingRunnable, execDetail.getCronExpr());
        ScheduledTask scheduledTask = new ScheduledTask(cronTask);
        try {
            scheduledTask.future = taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        } catch (TaskRejectedException ex) {
            logger.error("[EasyCronScheduler] Task: [key-{}] failed to start, task rejected", taskKey);
            updateTaskSate(taskKey, ExecDetail.ExecState.BLOCKED);
            return;
        }

        // 保存定时任务
        scheduledTasks.put(taskKey, scheduledTask);

        // 更新任务状态为运行中
        updateTaskSate(taskKey, ExecDetail.ExecState.RUNNING);
    }

    private boolean setExpireMonitor(String taskKey, LocalDateTime endTime) {
        if (null == endTime) return true; // 永久任务
        if (endTime.isBefore(LocalDateTime.now())) return false; // 已过期

        // 在endTime时间点之后，结束任务
        FixedTimeTask fixedTimeTask = new FixedTimeTask(() -> cancelTask(taskKey), endTime);
        ScheduledTask expireMonitor = new ScheduledTask(fixedTimeTask);
        expireMonitor.future = taskScheduler.schedule(
                fixedTimeTask.getRunnable(),
                Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));

        // 保存监控任务
        expireMonitors.put(ScheduleContext.MONITOR_TASK_PREFIX + taskKey, expireMonitor);

        return true;
    }

    private Object getBean(String beanName, String beanClassName) {
        try {
            Class<?> beanClass = Class.forName(beanClassName);
            // 先根据类型获取，再根据名称获取
            try {
                return ScheduleContext.applicationContext.getBean(beanClass);
            } catch (NoUniqueBeanDefinitionException ex) {
                logger.trace("[EasyCronScheduler] Bean of type {} is not unique", beanClass.getName());
                try {
                    return ScheduleContext.applicationContext.getBean(beanClass, beanName);
                } catch (NoSuchBeanDefinitionException ex2) {
                    logger.debug("[EasyCronScheduler] Bean of type {} with name {} not found",
                            beanClass.getName(), beanName);
                    return null;
                }
            } catch (NoSuchBeanDefinitionException ex) {
                logger.debug("[EasyCronScheduler] Bean of type {} not found", beanClass.getName());
                return null;
            }
        } catch (ClassNotFoundException ex) {
            logger.debug("[EasyCronScheduler] Bean class {} not found", beanClassName);
            return null;
        }
    }

    private void updateTaskSate(String taskKey, ExecDetail.ExecState state) {
        ExecDetail execDetail = ScheduleContext.taskStore.getExecDetail(taskKey);
        execDetail.setState(state);
        ScheduleContext.taskStore.updateExecDetail(execDetail);
    }

    @Override
    public void destroy() {
        // 1.停止系统任务
        systemTasks.forEach((key, task) -> task.cancel());
        // 2.停止已被调度的任务，更新任务状态为暂停
        scheduledTasks.keySet().forEach(this::pauseTask);
        // 3.停止结束时间监控任务
        expireMonitors.forEach((key, task) -> task.cancel());
    }
}
