package cn.comradexy.middleware.ecs.support.storage;

import cn.comradexy.middleware.ecs.domain.ErrorMsg;
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
     * 插入错误信息
     */
    void insertErrorMsg(String execDetailKey, String errorMsg);

    /**
     * 查询错误信息
     */
    ErrorMsg queryErrorMsg(String execDetailKey);

    /**
     * 查询所有错误信息
     */
    Set<ErrorMsg> queryAllErrorMsgs();

    /**
     * 删除错误信息
     */
    void deleteErrorMsg(String execDetailKey);

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
