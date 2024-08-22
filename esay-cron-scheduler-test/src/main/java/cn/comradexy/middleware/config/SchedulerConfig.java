package cn.comradexy.middleware.config;

import cn.comradexy.middleware.job.ScheduledJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-23
 * @Description: 配置类
 */
public class SchedulerConfig {
    @Bean
    public ScheduledJob scheduledJob() {
        return new ScheduledJob();
    }
}
