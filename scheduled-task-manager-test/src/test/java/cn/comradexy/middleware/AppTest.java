package cn.comradexy.middleware;

import cn.comradexy.middleware.sdk.domain.Job;
import cn.comradexy.middleware.sdk.support.storage.jdbc.JdbcStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 应用测试
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-15
 * @Description: 应用测试
 */
@SpringBootTest(classes = Application.class)
public class AppTest {
    @Resource
    private JdbcStorageService jdbcStorageService;

    @Test
    public void jdbcStorageServiceTest() {
        jdbcStorageService.insertJob(
                Job.builder().
                        key("key").
                        desc("desc").
                        beanClassName("beanClassName").
                        beanName("beanName").
                        methodName("methodName").
                        build()
        );
    }
}
