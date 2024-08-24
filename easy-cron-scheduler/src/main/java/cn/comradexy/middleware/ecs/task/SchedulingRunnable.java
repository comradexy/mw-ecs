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

    public SchedulingRunnable(String taskKey, Runnable runnable) {
        this.taskKey = taskKey;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        ExecDetail execDetail = ScheduleContext.taskStore.getExecDetail(taskKey);

        if (execDetail.getExecCount() >= execDetail.getMaxExecCount()) {
            ScheduleContext.scheduler.cancelTask(taskKey);
            return;
        }

        if (execDetail.getEndTime() != null && LocalDateTime.now().isAfter(execDetail.getEndTime())) {
            ScheduleContext.scheduler.cancelTask(taskKey);
            return;
        }

        ScheduleContext.taskStore.updateExecDetail(taskKey, LocalDateTime.now(), execDetail.getExecCount() + 1);

        try {
            this.runnable.run();
        } catch (RuntimeException e) {
            // 1.记录异常日志
            logger.error("[EasyCronScheduler] Task execution error, task key: {}", taskKey, e);
            // 2.任务执行状态切换为ERROR
            ScheduleContext.taskStore.updateExecState(taskKey, ExecDetail.ExecState.ERROR);
            // TODO: 上报异常
        }
    }
}
