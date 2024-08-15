package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;

import java.util.Set;

/**
 * 任务存储服务接口
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 任务存储服务接口
 */
public interface IJobStore {
    void addJob(Job job);

    void addExecDetail(ExecDetail execDetail);

    void deleteExecDetail(String execDetailKey);

    void deleteJob(String jobKey);

    Job getJob(String jobKey);

    ExecDetail getExecDetail(String execDetailKey);

    Set<Job> getAllJobs();

    Set<ExecDetail> getAllExecDetails();

    /**
     * 更新任务
     */
    void updateExecDetail(ExecDetail execDetail);

    /**
     * 保存任务及执行细节
     */
    void save();

}
