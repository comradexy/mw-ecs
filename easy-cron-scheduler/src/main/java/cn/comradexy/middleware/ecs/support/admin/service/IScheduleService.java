package cn.comradexy.middleware.ecs.support.admin.service;

import cn.comradexy.middleware.ecs.support.admin.domain.ExecDetailDTO;
import cn.comradexy.middleware.ecs.support.admin.domain.TaskHandlerDTO;

import java.util.List;

/**
 * 定时任务服务接口
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-11
 * @Description: 定时任务服务接口
 */
public interface IScheduleService {
    /**
     * 查询所有任务信息
     */
    List<ExecDetailDTO> queryAllTasks();

    /**
     * 查询任务信息
     */
    ExecDetailDTO queryTask(String key);

    /**
     * 调度任务
     */
    void scheduleTask(String taskKey);

    /**
     * 取消任务
     */
    void cancelTask(String taskKey);

    /**
     * 暂停任务
     */
    void pasueTask(String taskKey);

    /**
     * 恢复任务
     */
    void resumeTask(String taskKey);

    /**
     * 删除任务
     */
    void deleteTask(String taskKey);

    /**
     * 查询任务处理器
     */
    TaskHandlerDTO queryHandler(String handlerKey);

    /**
     * 更新任务
     */
    void updateTask(ExecDetailDTO execDetailDTO);
}
