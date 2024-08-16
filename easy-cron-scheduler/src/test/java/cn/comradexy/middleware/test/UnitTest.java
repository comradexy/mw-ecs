package cn.comradexy.middleware.test;

import cn.comradexy.middleware.ecs.domain.ExecDetail;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.Task;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    public void mapTest() {
        Map<String, ExecDetail> map = new ConcurrentHashMap<>();
        ExecDetail execDetail = ExecDetail.builder().
                key("key").
                desc("desc").
                cronExpr("cronExpr").
                taskHandlerKey("handlerKey")
                .build();
        map.put(execDetail.getKey(), execDetail);
        System.out.println(JSON.toJSONString(map.get("key")));
        System.out.println(JSON.toJSONString(map.get("key1")));
    }

    @Test
    public void endTimeMonitoringTest() throws InterruptedException {
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

    @Test
    public void dateTest() {
        Date date = new Date(0);
        System.out.println(date);
    }

    @Test
    public void classTest() {
        Task task = new CronTask(() -> System.out.println("Hello"), "0/3 * * * * ?");
        System.out.println(task.getClass());

        Object obj = task;
        System.out.println(obj.getClass());
    }

}
