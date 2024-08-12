package cn.comradexy.middleware.sdk.config;

import cn.comradexy.middleware.sdk.common.ScheduleContext;
import cn.comradexy.middleware.sdk.task.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时任务管理中心自动配置
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-22
 * @Description: 定时任务管理中心自动配置
 */
@Configuration
public class EasyCronSchedulerConfiguration {
    @Bean
    public Scheduler scheduler() {
        // 创建定时任务调度器
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 设置线程池容量
        taskScheduler.setPoolSize(ScheduleContext.Global.schedulerPoolSize);
        // 设置线程名前缀
        taskScheduler.setThreadNamePrefix("scheduler-thread-");
        // 等待任务在关机时完成--表明等待所有线程执行完
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时长
        taskScheduler.setAwaitTerminationSeconds(30);

        return new Scheduler(taskScheduler);
    }
}
