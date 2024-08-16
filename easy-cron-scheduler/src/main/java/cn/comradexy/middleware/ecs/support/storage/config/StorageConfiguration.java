package cn.comradexy.middleware.ecs.support.storage.config;

import cn.comradexy.middleware.ecs.config.EasyCronSchedulerProperties;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.support.storage.IStorageService;
import cn.comradexy.middleware.ecs.support.storage.jdbc.JdbcStorageService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 存储服务配置
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 存储服务配置
 */
@Configuration
@ConditionalOnProperty(prefix = "comradexy.middleware.scheudle", name = "enableStorage", havingValue = "true")
@MapperScan("cn.comradexy.middleware.ecs.support.storage.jdbc.mapper")
public class StorageConfiguration {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EasyCronSchedulerProperties properties;

    @Autowired
    public StorageConfiguration(EasyCronSchedulerProperties properties) {
        this.properties = properties;
    }

    @Bean("comradexy-middleware-storage-service")
    public IStorageService storageService() {
        if (properties.getStorageType().equals(EasyCronSchedulerProperties.StorageType.JDBC.getValue())) {
            return new JdbcStorageService();
        } else if (properties.getStorageType().equals(EasyCronSchedulerProperties.StorageType.REDIS.getValue())) {
            // TODO: Redis存储服务
            logger.warn("暂不支持的存储类型：{}", properties.getStorageType());
            return null;
        } else {
            logger.warn("未知的存储类型：{}", properties.getStorageType());
            return null;
        }
    }

    @Bean("comradexy-middleware-data-source")
    @ConditionalOnProperty(prefix = "comradexy.middleware.scheudle", name = "storageType", havingValue = "jdbc")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(properties.getDataSource().getUrl())
                .username(properties.getDataSource().getUsername())
                .password(properties.getDataSource().getPassword())
                .build();
    }

    @Bean("comradexy-middleware-sql-session-factory")
    @ConditionalOnProperty(prefix = "comradexy.middleware.scheudle", name = "storageType", havingValue = "jdbc")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("comradexy-middleware-data-source") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setTransactionFactory(new JdbcTransactionFactory());
        sqlSessionFactoryBean.setDefaultEnumTypeHandler(ExecDetail.ExecSateTypeHandler.class);
        return sqlSessionFactoryBean.getObject();
    }
}
