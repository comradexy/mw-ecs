package cn.comradexy.middleware.sdk.domain;

/**
 * 任务实体
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 任务实体
 */
public class Job {
    private String key; // appName + beanName + methodName
    private String desc;
    private Class<?> beanClass;
    private String beanName;
    private String methodName;
    private boolean durable;
    private boolean shouldRecover;



}
