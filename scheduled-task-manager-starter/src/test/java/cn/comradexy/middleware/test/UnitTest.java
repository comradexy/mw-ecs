package cn.comradexy.middleware.test;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * 单元测试
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 单元测试
 */
public class UnitTest {
    @Test
    public void test() throws InterruptedException {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(8);
        taskScheduler.initialize();

        // 设置一个5s后执行的任务
        Date startTime = new Date();
        System.out.println("Start time: " + startTime);
        taskScheduler.schedule(() -> {
            System.out.println("Execute time: " + new Date());
        }, new Date(startTime.getTime() + 5000));

        new CountDownLatch(1).await();
    }
}
