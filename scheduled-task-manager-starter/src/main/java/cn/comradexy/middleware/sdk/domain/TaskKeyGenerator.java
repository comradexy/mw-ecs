package cn.comradexy.middleware.sdk.domain;

import cn.comradexy.middleware.sdk.common.ScheduleContext;
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
        String key =
                schedulerServerId + "_" + taskHandler.getBeanClassName() + "_" + taskHandler.getBeanName() + "_" + taskHandler.getMethodName();
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
        String key = getTaskHandlerKey(schedulerServerId, taskHandler) + "_" + execDetail.getCronExpr() + "_" + endTime
                + execDetail.getMaxExecCount();
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
