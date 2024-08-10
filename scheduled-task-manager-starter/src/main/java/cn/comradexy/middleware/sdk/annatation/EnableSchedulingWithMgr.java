package cn.comradexy.middleware.sdk.annatation;

import cn.comradexy.middleware.sdk.config.ScheduledTaskMangerConfig;
import cn.comradexy.middleware.sdk.config.ScheduledWithMgrAnnotationProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * 开启使用管理中心托管的定时任务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-24
 * @Description: 开启使用管理中心托管的定时任务
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({
        ScheduledWithMgrAnnotationProcessor.class,
        ScheduledTaskMangerConfig.class
})
@Documented
public @interface EnableSchedulingWithMgr {
}
