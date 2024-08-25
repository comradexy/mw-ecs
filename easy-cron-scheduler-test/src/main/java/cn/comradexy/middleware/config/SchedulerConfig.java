package cn.comradexy.middleware.config;

import cn.comradexy.middleware.job.ScheduledTask;
import org.springframework.context.annotation.Bean;

/**
 * 配置类
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-23
 * @Description: 配置类
 */
public class SchedulerConfig {
    @Bean
    public ScheduledTask scheduledJob() {
        return new ScheduledTask();
    }
}
