package cn.comradexy.middleware.sdk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 自动配置类
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-11
 * @Description: 自动配置类
 */
@Configuration
@EnableConfigurationProperties(StarterProperties.class)
@Setter
@Getter
public class StarterAutoConfig {
    @Resource
    private StarterProperties properties;
}
