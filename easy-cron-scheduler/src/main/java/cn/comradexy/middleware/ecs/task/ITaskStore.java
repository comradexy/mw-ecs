package cn.comradexy.middleware.ecs.task;

import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.domain.TaskHandler;

import java.util.Set;

/**
 * 任务存储服务接口
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 任务存储服务接口
 */
public interface ITaskStore {
    /**
     * 添加任务处理器
     */
    void addTaskHandler(TaskHandler job);

    /**
     * 添加任务处理器
     */
    void addExecDetail(ExecDetail execDetail);

    /**
     * 删除任务
     */
    void deleteExecDetail(String execDetailKey);

    /**
     * 删除任务处理器
     */
    void deleteTaskHandler(String taskHandlerKey);

    /**
     * 根据 taskHandlerKey 查询 TaskHandler
     */
    TaskHandler getTaskHandler(String taskHandlerKey);

    /**
     * 根据 execDetailKey 查询 ExecDetail
     */
    ExecDetail getExecDetail(String execDetailKey);

    /**
     * 根据 taskHandlerKey 查询所有的 ExecDetail
     */
    Set<ExecDetail> getExecDetailsByTaskHandlerKey(String taskHandlerKey);

    /**
     * 获取所有任务处理器
     */
    Set<TaskHandler> getAllTaskHandlers();

    /**
     * 获取所有执行详情
     */
    Set<ExecDetail> getAllExecDetails();

    /**
     * 更新任务
     */
    void updateExecDetail(ExecDetail execDetail);

}
