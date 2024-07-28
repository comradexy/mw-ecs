package cn.comradexy.middleware.sdk.annatation;

import java.lang.annotation.*;

/**
 * 用于包含多个@ScheduledWithMgr注解
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-28
 * @Description: 用于包含多个@ScheduledWithMgr注解
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SchedulesWithMgr {
    ScheduledWithMgr[] value();
}
