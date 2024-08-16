package cn.comradexy.middleware.ecs.annatation;

import cn.comradexy.middleware.ecs.common.ScheduleContext;

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

    String desc() default "缺省";

    String endTime() default ScheduleContext.DEFAULT_END_TIME;

    long maxExecCount() default Long.MAX_VALUE;
}
