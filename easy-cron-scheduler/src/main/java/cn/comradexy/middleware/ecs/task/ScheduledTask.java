package cn.comradexy.middleware.ecs.task;

import org.springframework.scheduling.config.Task;

import java.util.concurrent.ScheduledFuture;

/**
 * 被调度的任务
 * <p>
 * 原Spring的ScheduledTask类的构造函数是包访问权限，无法直接使用，所以这里重新定义了一个ScheduledTask类
 * </p>
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 替换Spring的ScheduledTask
 */
public class ScheduledTask {
    private final Task task;

    volatile ScheduledFuture<?> future;

    ScheduledTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

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

    @Override
    public String toString() {
        return this.task.toString();
    }

}
