package cn.comradexy.middleware.sdk.task;

import org.springframework.scheduling.config.CronTask;

import java.util.concurrent.ScheduledFuture;

/**
 * 替换Spring的ScheduledTask
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 替换Spring的ScheduledTask
 */
public class ScheduledTask {

    volatile ScheduledFuture<?> future;

    /**
     * 取消定时任务
     */
    public void cancel() {
        this.cancel(true);
    }

    public void cancel(boolean mayInterruptIfRunning) {
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(mayInterruptIfRunning);
        }
    }

    public boolean isCancelled() {
        ScheduledFuture<?> future = this.future;
        if (future == null) return true;
        return future.isCancelled();
    }

}
