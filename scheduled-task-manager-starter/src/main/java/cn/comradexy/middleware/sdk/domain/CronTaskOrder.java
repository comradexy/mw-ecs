package cn.comradexy.middleware.sdk.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * 定时任务订单
 * <p>
 * 存放待执行的任务信息，包括类对象、方法名称、任务描述、任务执行时间、任务状态等；
 * <br>
 * 通过类对象和方法名称，利用反射机制执行具体任务，并通过cron表达式控制任务执行时间。
 * </p>
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-08
 * @Description: 定时任务订单
 */
@Setter
@Getter
public class CronTaskOrder {
    /**
     * 类对象
     */
    @JSONField(serialize = false)
    private Object bean;

    /**
     * 类对象名称
     */
    private String beanName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 任务描述
     */
    private String desc;

    /**
     * Cron表达式
     */
    private String cron;

    /**
     * 自动开启
     * TODO: 删除，在BeanPostProcessor中实现
     */
    private Boolean autoStartup;
}
