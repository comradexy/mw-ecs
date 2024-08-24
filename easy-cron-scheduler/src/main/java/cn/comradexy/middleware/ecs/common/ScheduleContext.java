package cn.comradexy.middleware.ecs.common;

import cn.comradexy.middleware.ecs.config.EasyCronSchedulerProperties;
import cn.comradexy.middleware.ecs.support.storage.IStorageService;
import cn.comradexy.middleware.ecs.task.Scheduler;
import cn.comradexy.middleware.ecs.task.TaskStore;
import org.springframework.context.ApplicationContext;

/**
 * 定时任务上下文信息
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 定时任务上下文信息
 */
public class ScheduleContext {
    public static final String DEFAULT_END_TIME = "NEVER_EXPIRE";

    public static ApplicationContext applicationContext;
    public static EasyCronSchedulerProperties properties;
    public static Scheduler scheduler;
    public static TaskStore taskStore;
    public static IStorageService storageService;
}
