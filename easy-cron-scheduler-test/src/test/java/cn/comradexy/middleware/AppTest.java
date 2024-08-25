package cn.comradexy.middleware;

import cn.comradexy.middleware.ecs.domain.TaskHandler;
import cn.comradexy.middleware.ecs.support.storage.jdbc.JdbcStorageService;
import cn.comradexy.middleware.job.ScheduledTask;
import org.junit.jupiter.api.Test;
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
