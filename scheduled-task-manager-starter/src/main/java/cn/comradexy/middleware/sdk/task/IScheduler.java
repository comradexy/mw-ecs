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
     * @param taskKey 任务ID
     */
    void scheduleTask(String taskKey);

    /**
     * 停止任务
     *
     * @param taskKey 任务ID
     */
    void cancelTask(String taskKey);

    /**
     * 暂停任务
     *
     * @param taskKey 任务ID
     */
    void pauseTask(String taskKey);

    /**
     * 重启任务
     *
     * @param taskKey 任务ID
     */
    void resumeTask(String taskKey);

    /**
     * 设置任务过期监控
     *
     */
    void setExpireMonitor(String taskKey, Date endTime);
}
