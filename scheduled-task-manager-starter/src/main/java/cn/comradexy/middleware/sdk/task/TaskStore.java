package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.common.ScheduleContext;
import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.TaskHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务存储区
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-10
 * @Description: 任务存储区
 */
public class TaskStore implements ITaskStore {
    private final Map<String, TaskHandler> taskHandlerCache = new ConcurrentHashMap<>(64);
    private final Map<String, ExecDetail> execDetailCache = new ConcurrentHashMap<>(64);


    @Override
    public void addTaskHandler(TaskHandler taskHandler) {
        taskHandlerCache.put(taskHandler.getKey(), taskHandler);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (ScheduleContext.storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        ScheduleContext.storageService.insertTaskHandler(taskHandler);
    }

    @Override
    public void addExecDetail(ExecDetail execDetail) {
        execDetailCache.put(execDetail.getKey(), execDetail);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (ScheduleContext.storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        ScheduleContext.storageService.insertExecDetail(execDetail);
    }

    @Override
    public void deleteExecDetail(String execDetailKey) {
        execDetailCache.remove(execDetailKey);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (ScheduleContext.storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        ScheduleContext.storageService.deleteExecDetail(execDetailKey);
    }

    @Override
    public void deleteTaskHandler(String taskHandlerKey) {
        TaskHandler taskHandler = taskHandlerCache.remove(taskHandlerKey);
        // 查询 EXEC_DETAIL_MAP 中所有 taskHandlerKey==taskHandler.key 的 ExecDetail，然后删除
        execDetailCache.entrySet().removeIf(entry -> entry.getValue().getTaskHandlerKey().equals(taskHandler.getKey()));
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (ScheduleContext.storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        ScheduleContext.storageService.deleteTaskHandler(taskHandlerKey);
    }

    @Override
    public TaskHandler getTaskHandler(String taskHandlerKey) {
        return taskHandlerCache.get(taskHandlerKey);
    }

    @Override
    public ExecDetail getExecDetail(String execDetailKey) {
        return execDetailCache.get(execDetailKey);
    }

    @Override
    public Set<TaskHandler> getAllTaskHandlers() {
        return new HashSet<>(taskHandlerCache.values());
    }

    @Override
    public Set<ExecDetail> getAllExecDetails() {
        return new HashSet<>(execDetailCache.values());
    }

    @Override
    public void updateExecDetail(ExecDetail execDetail) {
        execDetailCache.put(execDetail.getKey(), execDetail);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (ScheduleContext.storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        ScheduleContext.storageService.updateExecDetail(execDetailCache.get(execDetail.getKey()));
    }

}
