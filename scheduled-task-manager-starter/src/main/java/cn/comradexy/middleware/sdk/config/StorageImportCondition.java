package cn.comradexy.middleware.sdk.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 存储服务引入条件
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 存储服务引入条件
 */
public class StorageImportCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Boolean enableStorage = context.getEnvironment()
                .getProperty("comradexy.middleware.scheudle.enableStorage", Boolean.class);
        return enableStorage != null && enableStorage;
    }
}
