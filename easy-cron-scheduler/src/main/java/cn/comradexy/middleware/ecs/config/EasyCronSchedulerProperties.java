package cn.comradexy.middleware.ecs.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置参数
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-11
 * @Description: 配置参数
 */
@ConfigurationProperties("comradexy.middleware.scheudle")
@Data
public class EasyCronSchedulerProperties {
    private String schedulerServerId;
    private String schedulerServerName;
    private Integer schedulerPoolSize = 8;
    private Boolean enableStorage = false;
    private Boolean enableAdmin = false;
    private String storageType = "jdbc";
    private DataSourceProperties dataSource;

    @Data
    public static class DataSourceProperties {
        private String url;
        private String username;
        private String password;
    }
}
