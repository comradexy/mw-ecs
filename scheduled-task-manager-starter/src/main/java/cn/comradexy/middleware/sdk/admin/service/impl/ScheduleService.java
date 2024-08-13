package cn.comradexy.middleware.sdk.admin.service.impl;

import cn.comradexy.middleware.sdk.admin.service.IScheduleService;
import cn.comradexy.middleware.sdk.domain.TaskDTO;
import cn.comradexy.middleware.sdk.task.IScheduler;
import cn.comradexy.middleware.sdk.task.JobStore;
import org.springframework.stereotype.Service;

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
@Service
public class ScheduleService implements IScheduleService {
    @Resource
    private IScheduler scheduler;

    public List<TaskDTO> queryAllTasks() {
        List<TaskDTO> tasks = new ArrayList<>();
        JobStore.EXEC_DETAIL_MAP.values().forEach((execDetail) -> tasks.add(TaskDTO.createTaskDTO(execDetail)));
        return tasks;
    }

    public TaskDTO queryTask(String key) {
        return TaskDTO.createTaskDTO(JobStore.EXEC_DETAIL_MAP.get(key));
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
