package cn.comradexy.middleware.sdk.annatation;

import cn.comradexy.middleware.sdk.config.AdminConfiguration;
import cn.comradexy.middleware.sdk.config.EasyCronSchedulerConfiguration;
import cn.comradexy.middleware.sdk.config.EasyCronSchedulerInitProcessor;
import cn.comradexy.middleware.sdk.config.StorageConfiguration;
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
@Documented
@Import({EasyCronSchedulerInitProcessor.class})
@ImportAutoConfiguration({
        EasyCronSchedulerConfiguration.class,
        AdminConfiguration.class,
        StorageConfiguration.class
})
public @interface EnableEzScheduling {
}
