package cn.comradexy.middleware.sdk.annatation;

import cn.comradexy.middleware.sdk.config.EasyCronSchedulerAdminConfigSelector;
import cn.comradexy.middleware.sdk.config.EasyCronSchedulerConfiguration;
import cn.comradexy.middleware.sdk.config.EasyCronSchedulerInitProcessor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * 启用EasyCronScheduler服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-24
 * @Description: 启用EasyCronScheduler服务
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({EasyCronSchedulerInitProcessor.class, EasyCronSchedulerAdminConfigSelector.class})
@ImportAutoConfiguration({EasyCronSchedulerConfiguration.class})
@Documented
public @interface EnableEzScheduling {
    boolean enableAdmin() default false;
}
