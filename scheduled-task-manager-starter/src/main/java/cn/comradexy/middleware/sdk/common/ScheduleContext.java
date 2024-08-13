package cn.comradexy.middleware.sdk.common;

import cn.comradexy.middleware.sdk.config.EasyCronSchedulerProperties;
import cn.comradexy.middleware.sdk.task.IScheduler;
import cn.comradexy.middleware.sdk.task.JobStore;
import org.springframework.context.ApplicationContext;

/**
 * 定时任务上下文信息
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 定时任务上下文信息
 */
public class ScheduleContext {
    public static String SYS_TASK_PREFIX = "SYSTEM_";
    public static String MONITOR_TASK_PREFIX = "MONITOR_";

    public static ApplicationContext applicationContext;
    public static JobStore jobStore;
    public static IScheduler scheduler;
    public static EasyCronSchedulerProperties properties;
}
