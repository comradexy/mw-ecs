package cn.comradexy.middleware.job;

import cn.comradexy.middleware.sdk.annatation.ScheduledWithMgr;
import cn.comradexy.middleware.sdk.annatation.SchedulesWithMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定时任务测试用例
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-29
 * @Description: 定时任务测试用例
 */
public class ScheduledJob {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @SchedulesWithMgr({
            @ScheduledWithMgr(cron = "0/3 * * * * ?"),
            @ScheduledWithMgr(cron = "0/6 * * * * ?")
    })
    public void test() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        logger.info("{}: 定时任务执行", currentTime);
    }
}
