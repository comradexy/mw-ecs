package cn.comradexy.middleware.sdk.domain;

/**
 * @Author: ComradeXY
 * @CreateTime: 2024-07-22
 * @Description: 定时任务管理服务
 */
public interface IScheduledTaskMgrService {
    /**
     * 启动任务
     *
     * @param taskId      任务ID
     * @param cronExpr    cron表达式
     * @param taskHandler 任务处理器
     */
    void startTask(String taskId, String cronExpr, Runnable taskHandler);

    /**
     * 停止任务
     *
     * @param taskId 任务ID
     */
    void stopTask(String taskId);

    /**
     * 更新任务
     *
     * @param taskId      任务ID
     * @param cronExpr    cron表达式
     * @param taskHandler 任务处理器
     */
    void updateTask(String taskId, String cronExpr, Runnable taskHandler);

    /**
     * 暂停任务
     *
     * @param taskId 任务ID
     */
    void pauseTask(String taskId);

    /**
     * 恢复任务
     *
     * @param taskId 任务ID
     */
    void resumeTask(String taskId);

    /**
     * 列出所有任务
     */
    void listTasks();

    /**
     * 获取任务
     *
     * @param taskId 任务ID
     */
    void getTask(String taskId);
}
