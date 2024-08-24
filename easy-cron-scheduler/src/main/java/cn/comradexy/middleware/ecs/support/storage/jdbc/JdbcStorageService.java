package cn.comradexy.middleware.ecs.support.storage.jdbc;

import cn.comradexy.middleware.ecs.domain.ErrorMsg;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.domain.TaskHandler;
import cn.comradexy.middleware.ecs.support.storage.IStorageService;
import cn.comradexy.middleware.ecs.support.storage.jdbc.mapper.ErrorMsgMapper;
import cn.comradexy.middleware.ecs.support.storage.jdbc.mapper.ExecDetailMapper;
import cn.comradexy.middleware.ecs.support.storage.jdbc.mapper.TaskHandlerMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * JDBC存储服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-12
 * @Description: JDBC存储服务
 */
public class JdbcStorageService implements IStorageService {

    private TaskHandlerMapper taskHandlerMapper;
    private ExecDetailMapper execDetailMapper;
    private ErrorMsgMapper errorMsgMapper;

    @Autowired
    public void setJobMapper(TaskHandlerMapper taskHandlerMapper) {
        this.taskHandlerMapper = taskHandlerMapper;
    }

    @Autowired
    public void setExecDetailMapper(ExecDetailMapper execDetailMapper) {
        this.execDetailMapper = execDetailMapper;
    }

    @Autowired
    public void setErrorMsgMapper(ErrorMsgMapper errorMsgMapper) {
        this.errorMsgMapper = errorMsgMapper;
    }

    @Override
    public void insertErrorMsg(String execDetailKey, String errorMsg) {
        errorMsgMapper.insert(execDetailKey, errorMsg);
    }

    @Override
    public ErrorMsg queryErrorMsg(String execDetailKey) {
        return errorMsgMapper.query(execDetailKey);
    }

    @Override
    public Set<ErrorMsg> queryAllErrorMsgs() {
        return errorMsgMapper.queryAll();
    }

    @Override
    public void deleteErrorMsg(String execDetailKey) {
        errorMsgMapper.delete(execDetailKey);
    }

    @Override
    public void insertTaskHandler(TaskHandler job) {
        taskHandlerMapper.addTaskHandler(job);
    }

    @Override
    public void updateJob(TaskHandler job) {
        taskHandlerMapper.updateTaskHandler(job);
    }

    @Override
    public void deleteTaskHandler(String jobKey) {
        taskHandlerMapper.deleteTaskHandler(jobKey);
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
        execDetailMapper.deleteExecDetail(execDetailKey);
    }

    @Override
    public Set<TaskHandler> queryAllTaskHandlers() {
        return taskHandlerMapper.queryAllTaskHandlers();
    }

    @Override
    public Set<ExecDetail> queryAllExecDetails() {
        return execDetailMapper.queryAllExecDetails();
    }
}
