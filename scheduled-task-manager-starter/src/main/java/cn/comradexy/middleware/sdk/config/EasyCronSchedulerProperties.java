package cn.comradexy.middleware.sdk.config;

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
    private String storageType = StorageType.JDBC.getValue();
    private DataSourceProperties dataSource;
    // TODO: cleanExistingData

    @Data
    public static class DataSourceProperties {
        private String url;
        private String username;
        private String password;
    }

    @Getter
    public enum StorageType {
        JDBC("jdbc"),  // JDBC
        REDIS("redis"); // Redis

        private final String value;

        StorageType(String value) {
            this.value = value;
        }
    }
}
