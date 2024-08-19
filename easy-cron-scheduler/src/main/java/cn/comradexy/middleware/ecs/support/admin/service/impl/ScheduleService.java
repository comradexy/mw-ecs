package cn.comradexy.middleware.ecs.support.admin.service.impl;

import cn.comradexy.middleware.ecs.common.ScheduleContext;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.support.admin.domain.ExecDetailDTO;
import cn.comradexy.middleware.ecs.support.admin.domain.TaskHandlerDTO;
import cn.comradexy.middleware.ecs.support.admin.service.IScheduleService;
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

    public void scheduleTask(String taskKey) {
        scheduler.scheduleTask(taskKey);
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

    public void deleteTask(String taskKey) {
        // 停止任务
        scheduler.cancelTask(taskKey);

        // 删除ExecDetail
        String taskHandlerKey = ScheduleContext.taskStore.getExecDetail(taskKey).getTaskHandlerKey();
        ScheduleContext.taskStore.deleteExecDetail(taskKey);

        // 检查对应的TaskHandler是否还有其他任务，没有则删除TaskHandler
        // TODO: taskStore新增方法--按taskHandlerKey查询ExecDetail
        if (ScheduleContext.taskStore.getAllExecDetails().stream()
                .noneMatch(execDetail -> execDetail.getTaskHandlerKey().equals(taskHandlerKey))) {
            ScheduleContext.taskStore.deleteTaskHandler(taskHandlerKey);
        }
    }

    public TaskHandlerDTO queryHandler(String handlerKey) {
        return TaskHandlerDTO.createTaskHandlerDTO(ScheduleContext.taskStore.getTaskHandler(handlerKey));
    }

    public void updateTask(ExecDetailDTO execDetailDTO) {
        // TODO:

        // 1.校验参数

        // 2.暂停任务

        // 3.更新任务

        // 4.恢复任务

    }
}
