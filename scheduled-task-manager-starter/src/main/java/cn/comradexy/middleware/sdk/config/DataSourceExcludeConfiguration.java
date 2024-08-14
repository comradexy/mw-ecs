package cn.comradexy.middleware.sdk.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * 排除数据源自动配置
 * <p>
 * 当enableStorage为false时，
 * 排除DataSourceAutoConfiguration自动装配，
 * 避免未配置数据源而导致启动失败。
 * </p>
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-14
 * @Description: 排除数据源自动配置
 */
@Configuration
@ConditionalOnProperty(prefix = "comradexy.middleware.scheudle", name = "enableStorage", havingValue = "false")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DataSourceExcludeConfiguration {
}
