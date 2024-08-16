package cn.comradexy.middleware.ecs.support.admin.config;

import cn.comradexy.middleware.ecs.support.admin.controller.AdminController;
import cn.comradexy.middleware.ecs.support.admin.service.IScheduleService;
import cn.comradexy.middleware.ecs.support.admin.service.impl.ScheduleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 管理端配置
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 管理端配置
 */
@Configuration
@ConditionalOnProperty(prefix = "comradexy.middleware.scheudle", name = "enableAdmin", havingValue = "true")
public class AdminConfiguration {
    @Bean("comradexy-middleware-admin-controller")
    public AdminController adminController() {
        return new AdminController();
    }

    @Bean("comradexy-middleware-schedule-service")
    public IScheduleService scheduleService() {
        return new ScheduleService();
    }
}
