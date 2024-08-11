package cn.comradexy.middleware.sdk.annatation;

import java.lang.annotation.*;

/**
 * 声明定时任务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-24
 * @Description: 声明定时任务
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(EzSchedules.class)
public @interface EzScheduled {
    String cron() default "";

}
