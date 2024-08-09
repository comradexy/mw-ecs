package cn.comradexy.middleware.sdk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时任务注册器
 * <p>
 * 负责定时任务的注册、调度、删除
 * </p>
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-08
 * @Description: 定时任务注册器
 */
@Component
public class ScheduleService{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(64);

    @Autowired
    public ScheduleService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }

    /**
     * 添加定时任务
     *
     */
    public void addCronTask(Runnable task, String cronExpr) {
        // TODO:

    }

    /**
     * 移除定时任务
     *
     */
    public void removeCronTask(String taskId) {
        // TODO:

    }
}
