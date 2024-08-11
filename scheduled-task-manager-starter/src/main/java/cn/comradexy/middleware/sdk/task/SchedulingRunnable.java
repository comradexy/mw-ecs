package cn.comradexy.middleware.sdk.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable增强类--任务调度
 * <p>
 * 扩展Runnable，增加异常捕获和任务执行状态切换功能
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
            this.runnable.run();
        } catch (Exception e) {
            // 1.记录异常日志
            logger.error("任务[{}]执行异常: ", taskKey, e);
            // 2.任务执行状态切换
            JobStore.setError(taskKey);
        }
    }
}
