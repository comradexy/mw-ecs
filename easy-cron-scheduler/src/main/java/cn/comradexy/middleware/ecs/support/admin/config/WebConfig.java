package cn.comradexy.middleware.ecs.support.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-16
 * @Description: Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/ecs_admin").setViewName("forward:/index.html");
    }
}
