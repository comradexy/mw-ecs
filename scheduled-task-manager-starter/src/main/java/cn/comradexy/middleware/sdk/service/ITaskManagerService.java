package cn.comradexy.middleware.sdk.service;

import cn.comradexy.middleware.sdk.domain.Result;
import cn.comradexy.middleware.sdk.domain.ScheduledTaskVO;
import cn.comradexy.middleware.sdk.constants.ServiceResponseStatusVO;

import java.util.List;

/**
 * 定时任务管理服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-22
 * @Description: 定时任务管理服务
 */
public interface ITaskManagerService {
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

    /**
     * 更新任务
     *
     * @param taskId      任务ID
     * @param cronExpr    cron表达式
     * @param taskHandler 任务处理器
     */
    ServiceResponseStatusVO updateTask(String taskId, String cronExpr, Runnable taskHandler);

    /**
     * 暂停任务
     *
     * @param taskId 任务ID
     */
    ServiceResponseStatusVO pauseTask(String taskId);

    /**
     * 恢复任务
     *
     * @param taskId 任务ID
     */
    ServiceResponseStatusVO resumeTask(String taskId);

    /**
     * 列出所有任务
     */
    List<ScheduledTaskVO> listTasks();

    /**
     * 获取任务
     *
     * @param taskId 任务ID
     */
    ScheduledTaskVO getTask(String taskId);
}
