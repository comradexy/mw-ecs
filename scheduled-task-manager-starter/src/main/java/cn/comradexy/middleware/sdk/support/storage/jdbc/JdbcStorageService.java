package cn.comradexy.middleware.sdk.support.storage.jdbc;

import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;
import cn.comradexy.middleware.sdk.support.storage.IStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC存储服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-12
 * @Description: JDBC存储服务
 */
public class JdbcStorageService implements IStorageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void insertJob(Job job) {
        // TODO
    }

    @Override
    public Job queryJob(String jobKey) {
        // TODO
        return null;
    }

    @Override
    public void updateJob(Job job) {
        // TODO
    }

    @Override
    public void deleteJob(String jobKey) {
        // TODO
    }

    @Override
    public void insertExecDetail(ExecDetail execDetail) {
        // TODO
    }

    @Override
    public ExecDetail queryExecDetail(String execDetailKey) {
        // TODO
        return null;
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
