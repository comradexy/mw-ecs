package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.domain.Result;

/**
 * 定时任务管理服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-22
 * @Description: 定时任务管理服务
 */
public interface ITaskManager {
    /**
     * 创建并启动任务
     *
     * @param cronExpr    cron表达式
     * @param taskHandler 任务处理器
     */
    Result<String> createTask(String cronExpr, Runnable taskHandler);

    /**
     * 停止任务
     *
     * @param taskId 任务ID
     */
    Result<String> cancelTask(String taskId);

}
