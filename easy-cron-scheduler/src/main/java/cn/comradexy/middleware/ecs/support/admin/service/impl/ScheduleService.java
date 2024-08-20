package cn.comradexy.middleware.ecs.support.admin.service.impl;

import cn.comradexy.middleware.ecs.common.ScheduleContext;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.support.admin.domain.ExecDetailDTO;
import cn.comradexy.middleware.ecs.support.admin.domain.TaskHandlerDTO;
import cn.comradexy.middleware.ecs.support.admin.service.IScheduleService;
import cn.comradexy.middleware.ecs.task.IScheduler;
import cn.comradexy.middleware.ecs.task.ITaskStore;
import org.springframework.beans.factory.annotation.Autowired;

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

    private IScheduler scheduler;
    private ITaskStore taskStore;

    @Autowired
    public void setScheduler(IScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Autowired
    public void setTaskStore(ITaskStore taskStore) {
        this.taskStore = taskStore;
    }

    public List<ExecDetailDTO> queryAllTasks() {
        List<ExecDetailDTO> tasks = new ArrayList<>();
        taskStore.getAllExecDetails().forEach((execDetail) -> tasks.add(ExecDetailDTO.createExecDetailDTO(execDetail)));
        return tasks;
    }

    public ExecDetailDTO queryTask(String key) {
        return ExecDetailDTO.createExecDetailDTO(taskStore.getExecDetail(key));
    }

    public void scheduleTask(String taskKey) {
        scheduler.scheduleTask(taskKey);
    }

    public void cancelTask(String taskKey) {
        // 停止任务
        scheduler.cancelTask(taskKey);

        // 删除ExecDetail
        String taskHandlerKey = taskStore.getExecDetail(taskKey).getTaskHandlerKey();
        taskStore.deleteExecDetail(taskKey);

        // 检查对应的TaskHandler是否还有其他任务，没有则删除TaskHandler
        if (taskStore.getExecDetailsByTaskHandlerKey(taskHandlerKey).isEmpty()) {
            taskStore.deleteTaskHandler(taskHandlerKey);
        }
    }

    public void pasueTask(String taskKey) {
        scheduler.pauseTask(taskKey);
    }

    public void resumeTask(String taskKey) {
        scheduler.resumeTask(taskKey);
    }

    public TaskHandlerDTO queryHandler(String handlerKey) {
        return TaskHandlerDTO.createTaskHandlerDTO(taskStore.getTaskHandler(handlerKey));
    }

    public void updateTask(ExecDetailDTO execDetailDTO) {
        // TODO:

        // 1.校验参数

        // 2.暂停任务

        // 3.更新任务

        // 4.恢复任务

    }
}
