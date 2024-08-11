package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.common.ScheduleContext;
import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
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
public class Scheduler implements IScheduler, ApplicationContextAware, DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ApplicationContext applicationContext;

    private TaskScheduler taskScheduler;

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

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        // 允许自定义TaskScheduler并注入
        // 也可以声明TaskScheduler为Bean，ScheduledWithMgrAnnotationProcessor中会识别并将其注入
        Assert.notNull(taskScheduler, "TaskScheduler must not be null");

        // 检查scheduledTasks是否为空，如果不为空，说明已经有任务在运行，不允许更换TaskScheduler
        if (!scheduledTasks.isEmpty()) {
            logger.warn("已有任务在运行，不允许更换TaskScheduler");
            return;
        }

        this.taskScheduler = taskScheduler;
    }

    public boolean hasTaskScheduler() {
        return taskScheduler != null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void scheduleTask(String taskKey) {
        // 清空缓存中失效的任务
        scheduledTasks.forEach((key, value) -> {
            if (ExecDetail.ExecState.RUNNING.getKey() != JobStore.getExecDetail(key).getState()) {
                if (!value.isCancelled()) value.cancel();
                scheduledTasks.remove(key);
            }
        });

        // 从JobStore中获取任务信息(Job和ExecDetail)
        ExecDetail execDetail = JobStore.getExecDetail(taskKey);
        Job job = JobStore.getJob(execDetail.getJobKey());

        // 组装任务
        Object bean = getBean(job.getBeanName(), job.getBeanClassName());
        Method method = ReflectionUtils.findMethod(bean.getClass(), job.getMethodName());
        SchedulingRunnable runnable = new SchedulingRunnable(taskKey, createRunnable(bean, method));
        CronTask cronTask = new CronTask(runnable, execDetail.getCronExpr());

        // 创建定时任务
        ScheduledTask scheduledTask = new ScheduledTask(cronTask);
        scheduledTask.future = taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());

        // 保存定时任务
        scheduledTasks.put(taskKey, scheduledTask);

        // 更新任务状态为运行中
        JobStore.setRunning(taskKey);
    }

    public Object getBean(String beanName, String beanClassName) {
        // 先根据类型获取，再根据名称获取
        try {
            Class<?> beanClass = Class.forName(beanClassName);
            try {
                return applicationContext.getBean(beanClass);
            } catch (NoUniqueBeanDefinitionException ex) {
                logger.trace("存在多个{}类型的Bean，尝试根据名称获取", beanClass.getName());
                try {
                    return applicationContext.getBean(beanClass, beanName);
                } catch (NoSuchBeanDefinitionException ex2) {
                    logger.error("未找到{}类型的Bean", beanClass.getName());
                    return null;
                }
            } catch (NoSuchBeanDefinitionException ex) {
                logger.error("未找到{}类型的Bean", beanClass.getName());
                return null;
            }
        } catch (ClassNotFoundException ex) {
            logger.error("未找到{}类型的Bean", beanClassName);
            return null;
        }
    }

    private Runnable createRunnable(Object bean, Method method) {
        return () -> {
            try {
                ReflectionUtils.makeAccessible(method);
                method.invoke(bean);
            } catch (InvocationTargetException ex) {
                ReflectionUtils.rethrowRuntimeException(ex.getTargetException());
            } catch (IllegalAccessException ex) {
                throw new UndeclaredThrowableException(ex);
            }
        };
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
        JobStore.setComplete(taskKey);
    }

    @Override
    public void pauseTask(String taskKey) {
        // 1.删除缓存中的任务
        ScheduledTask scheduledTask = scheduledTasks.remove(taskKey);

        // 2.停止正在执行任务
        if (null != scheduledTask) {
            scheduledTask.cancel();
        }

        // 3.更新任务状态为暂停
        JobStore.setPaused(taskKey);
    }

    @Override
    public void resumeTask(String taskKey) {
        // TODO: 实现任务重启

    }

    @Override
    public void setExpireMonitor(String taskKey, Date endTime) {
        // 在endTime时间点之后，结束任务
        FixedTimeTask fixedTimeTask = new FixedTimeTask(() -> cancelTask(taskKey), endTime);
        ScheduledTask expireMonitor = new ScheduledTask(fixedTimeTask);
        expireMonitor.future = taskScheduler.schedule(fixedTimeTask.getRunnable(), fixedTimeTask.getExecTime());

        // 保存监控任务
        expireMonitors.put(ScheduleContext.MONITOR_TASK_PREFIX + taskKey, expireMonitor);
    }

    @Override
    public void destroy() {
        // 1.停止所有任务
        // 1.1.停止系统任务
        systemTasks.forEach((key, task) -> task.cancel());
        // 1.2.停止已被调度的任务，更新任务状态为暂停
        scheduledTasks.keySet().forEach(this::pauseTask);
        // 1.3.停止结束时间监控任务
        expireMonitors.forEach((key, task) -> task.cancel());

        // 2.存储任务及执行细节
        JobStore.save();
    }
}
