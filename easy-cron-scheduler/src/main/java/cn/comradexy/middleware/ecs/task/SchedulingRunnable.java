package cn.comradexy.middleware.ecs.task;

import cn.comradexy.middleware.ecs.common.ScheduleContext;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Runnable增强类--任务调度
 * <p>
 * 扩展Runnable，增加异常捕获和任务执行状态切换等功能
 * </p>
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-11
 * @Description: Runnable增强类
 */
public class SchedulingRunnable implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(SchedulingRunnable.class);
    private final String taskKey;
    private final Runnable runnable;
    private final IScheduler scheduler;

    public SchedulingRunnable(String taskKey, Runnable runnable, IScheduler scheduler) {
        this.taskKey = taskKey;
        this.runnable = runnable;
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        ExecDetail execDetail = ScheduleContext.taskStore.getExecDetail(taskKey);

        if (execDetail.getExecCount() >= execDetail.getMaxExecCount()) {
            scheduler.cancelTask(taskKey);
            return;
        }

        if(execDetail.getEndTime() != null && LocalDateTime.now().isAfter(execDetail.getEndTime())) {
            scheduler.cancelTask(taskKey);
            return;
        }

        execDetail.setExecCount(execDetail.getExecCount() + 1);
        execDetail.setLastExecTime(LocalDateTime.now());
        ScheduleContext.taskStore.updateExecDetail(execDetail);

        try {
            this.runnable.run();
        } catch (RuntimeException e) {
            // 1.记录异常日志
            logger.error("[EasyCronScheduler] Task execution error, task key: {}", taskKey, e);
            // 2.任务执行状态切换
            execDetail.setState(ExecDetail.ExecState.ERROR);
            ScheduleContext.taskStore.updateExecDetail(execDetail);
            // TODO: 上报异常
        }
    }
}
