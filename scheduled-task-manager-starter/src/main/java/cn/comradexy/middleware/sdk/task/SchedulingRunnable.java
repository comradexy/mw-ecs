package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.common.ScheduleContext;
import cn.comradexy.middleware.sdk.domain.ExecDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

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
        try {
            ExecDetail execDetail = ScheduleContext.jobStore.getExecDetail(taskKey);
            execDetail.setLastExecTime(new Date());
            execDetail.setExecCount(execDetail.getExecCount() + 1);
            this.runnable.run();
        } catch (RuntimeException e) {
            // 1.记录异常日志
            logger.error("任务[{}]执行异常: ", taskKey, e);
            // 2.任务执行状态切换
            ScheduleContext.jobStore.setError(taskKey);
        }
    }
}
