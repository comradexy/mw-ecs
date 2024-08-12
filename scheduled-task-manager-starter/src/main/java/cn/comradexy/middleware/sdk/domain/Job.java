package cn.comradexy.middleware.sdk.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务实体
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 任务实体
 */
@Builder
@Data
@EqualsAndHashCode(of = {"beanClassName", "beanName", "methodName"})
public class Job {
    /**
     * 任务唯一标识
     * <p>
     * schedulerServerId + beanClassName + beanName + methodName
     * </p>
     */
    private String key;

    /**
     * 任务描述
     */
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
