package cn.comradexy.middleware.sdk.config;

import cn.comradexy.middleware.sdk.admin.controller.AdminController;
import cn.comradexy.middleware.sdk.admin.service.impl.ScheduleService;
import cn.comradexy.middleware.sdk.annatation.EnableEzScheduling;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * 管理端配置选择器
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 管理端配置选择器
 */
public class EasyCronSchedulerAdminConfigSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes =
                importingClassMetadata.getAnnotationAttributes(EnableEzScheduling.class.getName());
        if (null == attributes) return new String[]{};
        boolean enableAdmin = (boolean) attributes.get("enableAdmin");
        if (enableAdmin) {
            return new String[]{AdminController.class.getName(), ScheduleService.class.getName()};
        }
        return new String[]{};
    }
}
