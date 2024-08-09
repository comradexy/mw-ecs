package cn.comradexy.middleware;

import cn.comradexy.middleware.sdk.task.Scheduler;
import cn.comradexy.middleware.sdk.domain.Result;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        // 创建一个每隔2秒执行一次的任务
        Result<String> createRes = scheduledTaskMgr.createTask("0/2 * * * * ?", () -> {
            // 打印当前时间+任务执行
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) +
                    " 任务执行");
        });

        // 等待10秒
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 删除任务
        Result<String> cancelRes = scheduledTaskMgr.cancelTask(createRes.getData());
        if (cancelRes.isSuccess()) {
            System.out.println("任务删除成功");
        } else {
            System.out.println("任务删除失败");
        }


    }
}
