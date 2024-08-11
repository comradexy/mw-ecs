package cn.comradexy.middleware.sdk.domain;

import lombok.Builder;
import lombok.Data;

/**
 * 任务实体
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 任务实体
 */
@Builder
@Data
public class Job {
    private String key; // TODO: appName + beanName + methodName

    private String desc;

    /**
     * 任务宿主类 - 全限定名
     */
    private String beanClassName;

    /**
     * 任务宿主类 - Spring Bean 名称
     */
    private String beanName;

    /**
     * 任务方法名
     */
    private String methodName;

}
