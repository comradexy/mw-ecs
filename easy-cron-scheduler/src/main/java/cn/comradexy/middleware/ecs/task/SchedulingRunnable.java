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
            execDetail.setState(ExecDetail.ExecState.COMPLETE);
            ScheduleContext.taskStore.updateExecDetail(execDetail);
            return;
        }

        execDetail.setExecCount(execDetail.getExecCount() + 1);
        execDetail.setLastExecTime(LocalDateTime.now());
        ScheduleContext.taskStore.updateExecDetail(execDetail);

        try {
            this.runnable.run();
        } catch (RuntimeException e) {
            // 1.记录异常日志
            logger.error("任务[{}]执行异常: ", taskKey, e);
            // 2.任务执行状态切换
            execDetail.setState(ExecDetail.ExecState.ERROR);
            ScheduleContext.taskStore.updateExecDetail(execDetail);
        }
    }
}
