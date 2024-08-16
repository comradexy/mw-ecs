package cn.comradexy.middleware.ecs.task;

import org.springframework.scheduling.config.Task;

import java.time.LocalDateTime;

/**
 * 在固定时间执行的任务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-11
 * @Description: 在固定时间执行的任务
 */
public class FixedTimeTask extends Task {
    private final LocalDateTime execTime;

    public FixedTimeTask(Runnable runnable, LocalDateTime execTime) {
        super(runnable);
        this.execTime = execTime;
    }

    public LocalDateTime getExecTime() {
        return execTime;
    }
}
