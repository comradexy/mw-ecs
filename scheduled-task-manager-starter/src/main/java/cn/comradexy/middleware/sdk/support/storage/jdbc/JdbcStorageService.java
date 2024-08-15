package cn.comradexy.middleware.sdk.support.storage.jdbc;

import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;
import cn.comradexy.middleware.sdk.support.storage.IStorageService;
import cn.comradexy.middleware.sdk.support.storage.jdbc.mapper.ExecDetailMapper;
import cn.comradexy.middleware.sdk.support.storage.jdbc.mapper.JobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * JDBC存储服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-12
 * @Description: JDBC存储服务
 */
public class JdbcStorageService implements IStorageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    public Job queryJob(String jobKey) {
        return jobMapper.getJob(jobKey);
    }

    @Override
    public void updateJob(Job job) {
        // TODO
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
    public ExecDetail queryExecDetail(String execDetailKey) {
        return execDetailMapper.getExecDetail(execDetailKey);
    }

    @Override
    public void updateExecDetail(ExecDetail execDetail) {
        // TODO
    }

    @Override
    public void deleteExecDetail(String execDetailKey) {
        // TODO
    }

    @Override
    public void saveAll() {
        // TODO
    }

    @Override
    public void loadAll() {
        // TODO
    }
}
