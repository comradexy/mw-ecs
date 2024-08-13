package cn.comradexy.middleware.sdk.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 管理端引入条件
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 管理端引入条件
 */
public class AdminImportCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Boolean enableAdmin = context.getEnvironment()
                .getProperty("comradexy.middleware.scheudle.enableAdmin", Boolean.class);
        return enableAdmin != null && enableAdmin;
    }
}
