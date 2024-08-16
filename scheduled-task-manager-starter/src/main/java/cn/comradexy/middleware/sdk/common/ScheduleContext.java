package cn.comradexy.middleware.sdk.common;

import cn.comradexy.middleware.sdk.config.EasyCronSchedulerProperties;
import cn.comradexy.middleware.sdk.support.storage.IStorageService;
import cn.comradexy.middleware.sdk.task.ITaskStore;
import cn.comradexy.middleware.sdk.task.IScheduler;
import org.springframework.context.ApplicationContext;

/**
 * 定时任务上下文信息
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 定时任务上下文信息
 */
public class ScheduleContext {
    public static final String SYS_TASK_PREFIX = "SYSTEM_";
    public static final String MONITOR_TASK_PREFIX = "MONITOR_";
    public static final String DEFAULT_END_TIME = "NEVER_EXPIRE";

    public static ApplicationContext applicationContext;
    public static EasyCronSchedulerProperties properties;
    public static IScheduler scheduler;
    public static ITaskStore taskStore;
    public static IStorageService storageService;
}
