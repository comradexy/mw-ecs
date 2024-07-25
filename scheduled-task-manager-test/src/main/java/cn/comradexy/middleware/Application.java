package cn.comradexy.middleware;

import cn.comradexy.middleware.sdk.annatation.EnableSchedulingWithMgr;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 定时任务管理器Demo
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-23
 * @Description: 定时任务管理器Demo
 */
@SpringBootApplication
@EnableSchedulingWithMgr
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
