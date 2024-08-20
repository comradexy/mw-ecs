package cn.comradexy.middleware.ecs.support.storage;

import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.domain.TaskHandler;

import java.util.Set;

/**
 * 存储服务接口
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-12
 * @Description: 存储服务接口
 */
public interface IStorageService {
    /**
     * 插入任务
     */
    void insertTaskHandler(TaskHandler job);

    /**
     * 更新任务
     */
    void updateJob(TaskHandler job);

    /**
     * 删除任务
     */
    void deleteTaskHandler(String jobKey);

    /**
     * 插入执行详情
     */
    void insertExecDetail(ExecDetail execDetail);

    /**
     * 更新执行详情
     */
    void updateExecDetail(ExecDetail execDetail);

    /**
     * 删除执行详情
     */
    void deleteExecDetail(String execDetailKey);

    /**
     * 查询所有执行器
     */
    Set<TaskHandler> queryAllTaskHandlers();

    /**
     * 查询所有执行详情
     */
    Set<ExecDetail> queryAllExecDetails();
}
