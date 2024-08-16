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
    void addTaskHandler(TaskHandler job);

    void addExecDetail(ExecDetail execDetail);

    void deleteExecDetail(String execDetailKey);

    void deleteTaskHandler(String taskHandlerKey);

    TaskHandler getTaskHandler(String taskHandlerKey);

    ExecDetail getExecDetail(String execDetailKey);

    Set<TaskHandler> getAllTaskHandlers();

    Set<ExecDetail> getAllExecDetails();

    /**
     * 更新任务
     */
    void updateExecDetail(ExecDetail execDetail);

}
