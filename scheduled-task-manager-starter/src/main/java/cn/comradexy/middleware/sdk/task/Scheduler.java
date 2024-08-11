package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.common.ScheduleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
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
    public void scheduleTask(String cronExpr, Runnable taskHandler) {
        // TODO：更新为从JobStore中获取任务信息(Job和ExecDetail)

        try {
            // 校验cron表达式合法性
            if (!CronExpression.isValidExpression(cronExpr)) {
                logger.error("cron表达式[{}]不合法", cronExpr);
            }

            // 生成任务ID
            String taskId = UUID.randomUUID().toString();

            // 创建定时任务
            CronTask cronTask = new CronTask(taskHandler, cronExpr);
            ScheduledTask scheduledTask = new ScheduledTask(cronTask);
            scheduledTask.future = taskScheduler.schedule(taskHandler, new CronTrigger(cronExpr));

            // 保存定时任务
            scheduledTasks.put(taskId, scheduledTask);

            logger.info("任务[{}]已启动", taskId);
        } catch (Exception e) {
            logger.error("启动任务失败", e);
        }
    }

    @Override
    public void cancelTask(String taskId) {
        // 1.删除缓存中的任务
        ScheduledTask scheduledTask = scheduledTasks.remove(taskId);

        // 2.停止正在执行任务
        if (null != scheduledTask) {
            scheduledTask.cancel();
        }

        // 3.更新任务状态为已完成
        JobStore.setComplete(taskId);
    }

    @Override
    public void pauseTask(String taskId) {
        // 1.删除缓存中的任务
        ScheduledTask scheduledTask = scheduledTasks.remove(taskId);

        // 2.停止正在执行任务
        if (null != scheduledTask) {
            scheduledTask.cancel();
        }

        // 3.更新任务状态为暂停
        JobStore.setPaused(taskId);
    }

    @Override
    public void resumeTask(String taskId) {
        // TODO: 实现任务重启

    }

    @Override
    public void setExpireMonitor(String taskId, Date endTime) {
        // 在endTime时间点之后，结束任务
        FixedTimeTask fixedTimeTask = new FixedTimeTask(() -> cancelTask(taskId), endTime);
        ScheduledTask expireMonitor = new ScheduledTask(fixedTimeTask);
        expireMonitor.future = taskScheduler.schedule(fixedTimeTask.getRunnable(), fixedTimeTask.getExecTime());

        // 保存监控任务
        expireMonitors.put(ScheduleContext.MONITOR_TASK_PREFIX + taskId, expireMonitor);
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
