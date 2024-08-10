package cn.comradexy.middleware;

import cn.comradexy.middleware.sdk.task.Scheduler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 测试用例
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-23
 * @Description: 测试用例
 */
@SpringBootTest
public class AppTest {
    @Resource
    private Scheduler scheduledTaskMgr;

    @Test
    public void test() {


    }
}
