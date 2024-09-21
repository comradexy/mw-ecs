package cn.comradexy.middleware;

import cn.comradexy.middleware.ecs.domain.TaskHandler;
import cn.comradexy.middleware.ecs.support.storage.jdbc.JdbcStorageService;
import cn.comradexy.middleware.job.ScheduledTask;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.annotation.EnableRetry;

import javax.annotation.Resource;

/**
 * 应用测试
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-15
 * @Description: 应用测试
 */
@EnableRetry
@SpringBootTest(classes = Application.class)
public class AppTest {
    @Resource
    private JdbcStorageService jdbcStorageService;

    @Resource
    private ScheduledTask scheduledTask;

    @Value("${zk.connectString}")
    private String zkConnectString;

    @Value("${zk.scheme}")
    private String scheme;

    @Value("${zk.auth}")
    private String auth;

    @Test
    public void curatorTest() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        try (CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zkConnectString)
                .sessionTimeoutMs(6000)
                .connectionTimeoutMs(6000)
                .retryPolicy(retryPolicy)
                .authorization(scheme, auth.getBytes())
                .build()) {
            client.start();
            // 查询全部节点
            System.out.println("查询全部节点：" + client.getChildren().forPath("/"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void jdbcStorageServiceTest() {
        jdbcStorageService.insertTaskHandler(
                TaskHandler.builder().
                        key("key").
                        desc("desc").
                        beanClassName("beanClassName").
                        beanName("beanName").
                        methodName("methodName").
                        build()
        );
    }

}
