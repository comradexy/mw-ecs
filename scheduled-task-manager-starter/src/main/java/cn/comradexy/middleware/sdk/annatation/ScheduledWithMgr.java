package cn.comradexy.middleware.sdk.annatation;

import java.lang.annotation.*;

/**
 * 定时方法--使用管理中心托管
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-24
 * @Description: 定时方法--使用管理中心托管
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
//@Repeatable(SchedulesWithMgr.class)
public @interface ScheduledWithMgr {
    String cron() default "";
}
