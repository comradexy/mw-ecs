package cn.comradexy.middleware.sdk.support.storage.jdbc;

import cn.comradexy.middleware.sdk.common.ScheduleContext;
import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;
import cn.comradexy.middleware.sdk.support.storage.IStorageService;
import cn.comradexy.middleware.sdk.support.storage.jdbc.mapper.ExecDetailMapper;
import cn.comradexy.middleware.sdk.support.storage.jdbc.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * JDBC存储服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-12
 * @Description: JDBC存储服务
 */
public class JdbcStorageService implements IStorageService {

    private JobMapper jobMapper;

    private ExecDetailMapper execDetailMapper;

    @Autowired
    public void setJobMapper(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    @Autowired
    public void setExecDetailMapper(ExecDetailMapper execDetailMapper) {
        this.execDetailMapper = execDetailMapper;
    }

    @Override
    public void insertJob(Job job) {
        jobMapper.addJob(job);
    }

    @Override
    public void updateJob(Job job) {
        jobMapper.updateJob(job);
    }

    @Override
    public void deleteJob(String jobKey) {
        // TODO
        // 删除ecs_job.key = #{key}的job，同时删除ecs_exec_detail.job_key = #{key}的所有exec_detail
    }

    @Override
    public void insertExecDetail(ExecDetail execDetail) {
        execDetailMapper.addExecDetail(execDetail);
    }

    @Override
    public void updateExecDetail(ExecDetail execDetail) {
        execDetailMapper.updateExecDetail(execDetail);
    }

    @Override
    public void deleteExecDetail(String execDetailKey) {
        // TODO
    }

    @Override
    public void recover() {
        jobMapper.listJobs().forEach(job -> ScheduleContext.jobStore.addJob(job));
        execDetailMapper.listExecDetails().forEach(execDetail -> ScheduleContext.jobStore.addExecDetail(execDetail));
    }
}
