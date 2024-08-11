package cn.comradexy.middleware.sdk.task;

import org.springframework.scheduling.config.Task;

import java.util.Date;

/**
 * 在固定时间执行的任务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-11
 * @Description: 在固定时间执行的任务
 */
public class FixedTimeTask extends Task {
    private final Date execTime;

    public FixedTimeTask(Runnable runnable, Date execTime) {
        super(runnable);
        this.execTime = execTime;
    }

    public Date getExecTime() {
        return execTime;
    }
}
