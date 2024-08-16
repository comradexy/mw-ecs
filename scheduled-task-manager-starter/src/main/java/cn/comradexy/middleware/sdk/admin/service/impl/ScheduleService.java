package cn.comradexy.middleware.sdk.admin.service.impl;

import cn.comradexy.middleware.sdk.admin.service.IScheduleService;
import cn.comradexy.middleware.sdk.common.ScheduleContext;
import cn.comradexy.middleware.sdk.admin.domain.TaskDTO;
import cn.comradexy.middleware.sdk.task.IScheduler;

import javax.annotation.Resource;
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
    @Resource
    private IScheduler scheduler;

    public List<TaskDTO> queryAllTasks() {
        List<TaskDTO> tasks = new ArrayList<>();
        ScheduleContext.taskStore.getAllExecDetails().forEach((execDetail) -> tasks.add(TaskDTO.createTaskDTO(execDetail)));
        return tasks;
    }

    public TaskDTO queryTask(String key) {
        return TaskDTO.createTaskDTO(ScheduleContext.taskStore.getExecDetail(key));
    }

    public void cancelTask(String taskKey) {
        scheduler.cancelTask(taskKey);
    }

    public void pasueTask(String taskKey) {
        scheduler.pauseTask(taskKey);
    }

    public void resumeTask(String taskKey) {
        scheduler.resumeTask(taskKey);
    }
}
