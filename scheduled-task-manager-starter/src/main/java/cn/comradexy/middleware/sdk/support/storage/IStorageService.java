package cn.comradexy.middleware.sdk.support.storage;

import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;

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
    void insertJob(Job job);

    /**
     * 更新任务
     */
    void updateJob(Job job);

    /**
     * 删除任务
     */
    void deleteJob(String jobKey);

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
     * 数据恢复
     */
    void recover();
}
