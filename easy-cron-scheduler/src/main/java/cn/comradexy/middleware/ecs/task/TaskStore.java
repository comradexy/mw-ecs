package cn.comradexy.middleware.ecs.task;

import cn.comradexy.middleware.ecs.common.ScheduleContext;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.domain.TaskHandler;
import cn.comradexy.middleware.ecs.support.storage.IStorageService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private IStorageService storageService;

    @Nullable
    @Autowired
    public void setStorageService(IStorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void addTaskHandler(TaskHandler taskHandler) {
        taskHandlerCache.put(taskHandler.getKey(), taskHandler);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.insertTaskHandler(taskHandler);
    }

    @Override
    public void addExecDetail(ExecDetail execDetail) {
        execDetailCache.put(execDetail.getKey(), execDetail);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.insertExecDetail(execDetail);
    }

    @Override
    public void deleteExecDetail(String execDetailKey) {
        ExecDetail execDetail = execDetailCache.remove(execDetailKey);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.deleteExecDetail(execDetailKey);

        // 检查其处理器是否还有关联其他的执行详情
        if (execDetail != null) {
            String taskHandlerKey = execDetail.getTaskHandlerKey();
            if (getExecDetailsByTaskHandlerKey(taskHandlerKey).isEmpty()) {
                deleteTaskHandler(taskHandlerKey);
            }
        }
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
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.updateExecDetail(execDetailCache.get(execDetail.getKey()));
    }

    @Override
    public void recover() {
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }

        storageService.queryAllExecDetails().forEach(this::addExecDetail);
        storageService.queryAllTaskHandlers().forEach(this::addTaskHandler);
    }

    private void deleteTaskHandler(String taskHandlerKey) {
        taskHandlerCache.remove(taskHandlerKey);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.deleteTaskHandler(taskHandlerKey);
    }

    private Set<ExecDetail> getExecDetailsByTaskHandlerKey(String taskHandlerKey) {
        Set<ExecDetail> execDetails = new HashSet<>();
        execDetailCache.values().forEach(execDetail -> {
            if (execDetail.getTaskHandlerKey().equals(taskHandlerKey)) {
                execDetails.add(execDetail);
            }
        });
        return execDetails;
    }
}
