package cn.comradexy.middleware.sdk.task;

import java.util.Date;

/**
 * 定时任务调度器
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-22
 * @Description: 定时任务调度器
 */
public interface IScheduler {
    /**
     * 创建并启动任务
     *
     * @param cronExpr    cron表达式
     * @param taskHandler 任务处理器
     */
    void scheduleTask(String cronExpr, Runnable taskHandler);

    /**
     * 停止任务
     *
     * @param taskId 任务ID
     */
    void cancelTask(String taskId);

    /**
     * 暂停任务
     *
     * @param taskId 任务ID
     */
    void pauseTask(String taskId);

    /**
     * 重启任务
     *
     * @param taskId 任务ID
     */
    void resumeTask(String taskId);

    /**
     * 设置任务过期监控
     *
     */
    void setExpireMonitor(String taskId, Date endTime);
}
