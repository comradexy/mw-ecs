package cn.comradexy.middleware.ecs.support.admin.service.impl;

import cn.comradexy.middleware.ecs.support.admin.domain.TaskHandlerDTO;
import cn.comradexy.middleware.ecs.support.admin.service.IScheduleService;
import cn.comradexy.middleware.ecs.common.ScheduleContext;
import cn.comradexy.middleware.ecs.support.admin.domain.ExecDetailDTO;
import cn.comradexy.middleware.ecs.task.IScheduler;

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

    public List<ExecDetailDTO> queryAllTasks() {
        List<ExecDetailDTO> tasks = new ArrayList<>();
        ScheduleContext.taskStore.getAllExecDetails().forEach((execDetail) -> tasks.add(ExecDetailDTO.createExecDetailDTO(execDetail)));
        return tasks;
    }

    public ExecDetailDTO queryTask(String key) {
        return ExecDetailDTO.createExecDetailDTO(ScheduleContext.taskStore.getExecDetail(key));
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

    public TaskHandlerDTO queryHandler(String handlerKey) {
        return TaskHandlerDTO.createTaskHandlerDTO(ScheduleContext.taskStore.getTaskHandler(handlerKey));
    }
}
