package cn.comradexy.middleware.sdk.config;

import cn.comradexy.middleware.sdk.task.IJobStore;
import cn.comradexy.middleware.sdk.task.IScheduler;
import cn.comradexy.middleware.sdk.task.JobStore;
import cn.comradexy.middleware.sdk.task.Scheduler;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 定时任务配置
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-22
 * @Description: 定时任务配置
 */
@Configuration("comradexy-middleware-easy-cron-scheduler-configuration")
@EnableConfigurationProperties(EasyCronSchedulerProperties.class)
@Getter
public class EasyCronSchedulerConfiguration {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EasyCronSchedulerProperties properties;

    @Autowired
    public EasyCronSchedulerConfiguration(EasyCronSchedulerProperties properties) {
        this.properties = properties;
    }

    @Bean("comradexy-middleware-job-store")
    public IJobStore jobStore() {
        return new JobStore();
    }

    @Bean("comradexy-middleware-easy-cron-scheduler")
    public IScheduler scheduler() {
        // 创建定时任务调度器
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 设置线程池容量
        taskScheduler.setPoolSize(properties.getSchedulerPoolSize());
        // 设置线程名前缀
        taskScheduler.setThreadNamePrefix("scheduler-thread-");
        // 等待任务在关机时完成--表明等待所有线程执行完
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时长
        taskScheduler.setAwaitTerminationSeconds(30);
        // 初始化
        taskScheduler.initialize();

        return new Scheduler(taskScheduler);
    }

}
