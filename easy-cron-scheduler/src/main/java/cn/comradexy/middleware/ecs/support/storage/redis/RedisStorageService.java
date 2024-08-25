package cn.comradexy.middleware.ecs.support.storage.redis;

import cn.comradexy.middleware.ecs.domain.ErrorMsg;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.domain.TaskHandler;
import cn.comradexy.middleware.ecs.support.storage.IStorageService;

import java.util.Set;

/**
 * Redis存储服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-25
 * @Description: Redis存储服务
 */
public class RedisStorageService implements IStorageService {


    @Override
    public void insertErrorMsg(String execDetailKey, String errorMsg) {

    }

    @Override
    public ErrorMsg queryErrorMsg(String execDetailKey) {

        return null;
    }

    @Override
    public Set<ErrorMsg> queryAllErrorMsgs() {

        return null;
    }

    @Override
    public void deleteErrorMsg(String execDetailKey) {

    }

    @Override
    public void insertTaskHandler(TaskHandler job) {

    }

    @Override
    public void updateJob(TaskHandler job) {

    }

    @Override
    public void deleteTaskHandler(String jobKey) {

    }

    @Override
    public void insertExecDetail(ExecDetail execDetail) {

    }

    @Override
    public void updateExecDetail(ExecDetail execDetail) {

    }

    @Override
    public void deleteExecDetail(String execDetailKey) {

    }

    @Override
    public Set<TaskHandler> queryAllTaskHandlers() {

        return null;
    }

    @Override
    public Set<ExecDetail> queryAllExecDetails() {

        return null;
    }
}
