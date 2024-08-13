package cn.comradexy.middleware.sdk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置参数
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-11
 * @Description: 配置参数
 */
@ConfigurationProperties("comradexy.middleware.scheudle")
@Getter
@Setter
public class EasyCronSchedulerProperties {
    private String schedulerServerId;
    private String schedulerServerName;
    private Integer schedulerPoolSize = 8;
    private Boolean enableStorage = false;
    private Boolean enableAdmin = false;
}
