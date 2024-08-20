package cn.comradexy.middleware.ecs.support.storage.jdbc;

import cn.comradexy.middleware.ecs.common.ScheduleContext;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.domain.TaskHandler;
import cn.comradexy.middleware.ecs.support.storage.IStorageService;
import cn.comradexy.middleware.ecs.support.storage.jdbc.mapper.ExecDetailMapper;
import cn.comradexy.middleware.ecs.support.storage.jdbc.mapper.TaskHandlerMapper;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    public void setJobMapper(TaskHandlerMapper taskHandlerMapper) {
        this.taskHandlerMapper = taskHandlerMapper;
    }

    @Autowired
    public void setExecDetailMapper(ExecDetailMapper execDetailMapper) {
        this.execDetailMapper = execDetailMapper;
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
        // TODO: 查询 EXEC_DETAIL_MAP 中所有 taskHandlerKey==taskHandler.key 的 ExecDetail，然后删除
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
    public void recover() {
        execDetailMapper.listExecDetails().forEach(execDetail -> {
            // 如果任务状态为COMPLETE，则删除
            if (execDetail.getState().equals(ExecDetail.ExecState.COMPLETE)) {
                execDetailMapper.deleteExecDetail(execDetail.getKey());
                return;
            }
            ScheduleContext.taskStore.addExecDetail(execDetail);
        });

        taskHandlerMapper.listTaskHandlers().forEach(taskHandler -> {
            // 如果没有ExecDetail和TaskHandler绑定，则删除TaskHandler
            if(ScheduleContext.taskStore.getExecDetailsByTaskHandlerKey(taskHandler.getKey()).isEmpty()) {
                taskHandlerMapper.deleteTaskHandler(taskHandler.getKey());
                return;
            }
            ScheduleContext.taskStore.addTaskHandler(taskHandler);
        });

    }
}
