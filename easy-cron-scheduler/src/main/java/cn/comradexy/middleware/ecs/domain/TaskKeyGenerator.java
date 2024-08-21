package cn.comradexy.middleware.ecs.domain;

import cn.comradexy.middleware.ecs.common.ScheduleContext;
import org.springframework.util.DigestUtils;

/**
 * TaskHandler和ExecDetail的key生成器
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-16
 * @Description: TaskHandler和ExecDetail的key生成器
 */
public class TaskKeyGenerator {
    /**
     * 生成TaskHandler的key
     *
     * @param schedulerServerId 调度服务器ID
     * @param taskHandler       任务处理器
     * @return 任务处理器的key
     */
    public static String getTaskHandlerKey(String schedulerServerId, TaskHandler taskHandler) {
        return getTaskHandlerKey(schedulerServerId, taskHandler.getBeanClassName(), taskHandler.getBeanName(),
                taskHandler.getMethodName());
    }

    /**
     * 生成TaskHandler的key
     *
     * @param schedulerServerId 调度服务器ID
     * @param beanClassName     bean类名
     * @param beanName          bean名称
     * @param methodName        方法名
     * @return 任务处理器的key
     */
    public static String getTaskHandlerKey(String schedulerServerId, String beanClassName, String beanName,
                                           String methodName) {
        String key = schedulerServerId + "_" + beanClassName + "_" + beanName + "_" + methodName;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 生成ExecDetail的key
     *
     * @param schedulerServerId 调度服务器ID
     * @param taskHandler       任务处理器
     * @param execDetail        执行详情
     * @return 执行详情的key
     */
    public static String getExecDetailKey(String schedulerServerId, TaskHandler taskHandler, ExecDetail execDetail) {
        String endTime = execDetail.getEndTime() == null ? ScheduleContext.DEFAULT_END_TIME :
                execDetail.getEndTime().toString();
        return getExecDetailKey(schedulerServerId, taskHandler.getBeanClassName(), taskHandler.getBeanName(),
                taskHandler.getMethodName(), execDetail.getCronExpr(), endTime, execDetail.getMaxExecCount());
    }

    /**
     * 生成ExecDetail的key
     *
     * @param schedulerServerId 调度服务器ID
     * @param beanClassName     bean类名
     * @param beanName          bean名称
     * @param methodName        方法名
     * @param cronExpr          cron表达式
     * @param endTime           结束时间
     * @param maxExecCount      最大执行次数
     * @return 执行详情的key
     */
    public static String getExecDetailKey(String schedulerServerId, String beanClassName, String beanName,
                                          String methodName, String cronExpr, String endTime, Long maxExecCount) {
        String key = getTaskHandlerKey(schedulerServerId, beanClassName, beanName, methodName) + "_" + cronExpr + "_"
                + endTime + maxExecCount;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
