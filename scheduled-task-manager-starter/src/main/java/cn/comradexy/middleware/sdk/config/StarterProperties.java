package cn.comradexy.middleware.sdk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置参数
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-11
 * @Description: 配置参数
 */
@ConfigurationProperties("comradexy.middleware.scheudle")
public class StarterProperties {
    public int schedulePoolSize = 8;
    public String schedulerServerId;
    public String schedulerServerName;
    public Boolean enableStorage = false;
}
