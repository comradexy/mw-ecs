package cn.comradexy.middleware.trigger.job;

import cn.comradexy.middleware.sdk.domain.ScheduledTaskMgrService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 测试用任务类
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-23
 * @Description: 测试用任务类
 */
@Component
public class TestJob {
    @Resource
    private ScheduledTaskMgrService scheduledTaskMgrService;

    public void test() {
        // 创建一个每隔2秒执行一次的任务
        scheduledTaskMgrService.createTask("0/2 * * * * ?", () -> {
            System.out.println("Hello, World!");
        });
    }
}
