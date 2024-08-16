package cn.comradexy.middleware.ecs.annatation;

import java.lang.annotation.*;

/**
 * 包装多个@EzScheduledW注解
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-28
 * @Description: 包装多个@EzScheduledW注解
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EzSchedules {
    EzScheduled[] value();
}
