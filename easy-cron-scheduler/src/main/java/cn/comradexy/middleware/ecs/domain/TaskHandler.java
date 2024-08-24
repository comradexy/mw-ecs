package cn.comradexy.middleware.ecs.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 任务处理器实体
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 任务处理器实体
 */
@Builder
@Data
@EqualsAndHashCode(of = {"beanClassName", "beanName", "methodName"})
public class TaskHandler implements Serializable {
    /**
     * 任务唯一标识
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
