package cn.comradexy.middleware.sdk.common;

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

    public static JobStore jobStore;
    public static IScheduler scheduler;
    public static ApplicationContext applicationContext;
    public static String schedulerServerId;     //任务服务ID
    public static String schedulerServerName;   //任务服务名称
    public static int schedulerPoolSize;     //定时任务执行线程池核心线程数
    public static Boolean enableStorage; //是否开启任务存储
}
