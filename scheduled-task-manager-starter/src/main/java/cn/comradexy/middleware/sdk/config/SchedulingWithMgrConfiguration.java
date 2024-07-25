package cn.comradexy.middleware.sdk.config;

import cn.comradexy.middleware.sdk.annatation.EnableSchedulingWithMgr;
import cn.comradexy.middleware.sdk.annatation.ScheduledWithMgrAnnotationProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定时任务注解配置类
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-25
 * @Description: 定时任务注解配置类
 */
@Configuration()
@ConditionalOnBean(annotation = EnableSchedulingWithMgr.class) // 没有使用@EnableSchedulingWithMgr注解，就不加载这个类
public class SchedulingWithMgrConfiguration {
    public static final String DEFAULT_TASK_SCHEDULER_BEAN_NAME = "taskScheduler";
    public static final String DEFAULT_SCHEDULED_TASK_MGR_SERVICE_BEAN_NAME = "scheduledTaskMgrService";

    public SchedulingWithMgrConfiguration() {
    }

    @Bean
    public ScheduledWithMgrAnnotationProcessor scheduledWithMgrAnnotationProcessor() {
        return new ScheduledWithMgrAnnotationProcessor();
    }
}
