package cn.comradexy.middleware.ecs.support.admin.service;

import cn.comradexy.middleware.ecs.support.admin.domain.ExecDetailDTO;
import cn.comradexy.middleware.ecs.support.admin.domain.TaskHandlerDTO;
import cn.comradexy.middleware.ecs.support.admin.service.IScheduleService;
import cn.comradexy.middleware.ecs.task.Scheduler;
import cn.comradexy.middleware.ecs.task.TaskStore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 定时任务服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 定时任务服务
 */
public class ScheduleService implements IScheduleService {

    private Scheduler scheduler;
    private TaskStore taskStore;

    @Autowired
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Autowired
    public void setTaskStore(TaskStore taskStore) {
        this.taskStore = taskStore;
    }

    public void deleteTask(String taskKey) {
        scheduler.deleteTask(taskKey);
    }

    public void pasueTask(String taskKey) {
        scheduler.pauseTask(taskKey);
    }

    public void resumeTask(String taskKey) {
        scheduler.resumeTask(taskKey);
    }

    public List<ExecDetailDTO> queryAllTasks() {
        List<ExecDetailDTO> tasks = new ArrayList<>();
        taskStore.getAllExecDetails().forEach((execDetail) -> tasks.add(ExecDetailDTO.createExecDetailDTO(execDetail)));
        return tasks;
    }

    public ExecDetailDTO queryTask(String key) {
        return ExecDetailDTO.createExecDetailDTO(taskStore.getExecDetail(key));
    }

    public TaskHandlerDTO queryHandler(String handlerKey) {
        return TaskHandlerDTO.createTaskHandlerDTO(taskStore.getTaskHandler(handlerKey));
    }

    public String queryErrorMsg(String execDetailKey){
        return taskStore.getErrorMsg(execDetailKey).getErrorMsg();
    }
}
