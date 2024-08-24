package cn.comradexy.middleware.ecs.task;

import cn.comradexy.middleware.ecs.common.ScheduleContext;
import cn.comradexy.middleware.ecs.domain.ErrorMsg;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.domain.TaskHandler;
import cn.comradexy.middleware.ecs.support.storage.IStorageService;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
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
public class TaskStore {
    private final Map<String, TaskHandler> taskHandlerCache = new ConcurrentHashMap<>(64);
    private final Map<String, ExecDetail> execDetailCache = new ConcurrentHashMap<>(64);
    private final Map<String, ErrorMsg> errorMsgCache = new ConcurrentHashMap<>(64);

    private IStorageService storageService;

    @Autowired(required = false)
    public void setStorageService(IStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * 根据 taskHandlerKey 查询 TaskHandler (只读)
     */
    public TaskHandler getTaskHandler(String taskHandlerKey) {
        return (TaskHandler) SerializationUtils.clone(taskHandlerCache.get(taskHandlerKey));
    }

    /**
     * 根据 execDetailKey 查询 ExecDetail (只读)
     */
    public ExecDetail getExecDetail(String execDetailKey) {
        return (ExecDetail) SerializationUtils.clone(execDetailCache.get(execDetailKey));
    }

    /**
     * 根据 execDetailKey 查询 ErrorMsg (只读)
     */
    public ErrorMsg getErrorMsg(String execDetailKey) {
        return (ErrorMsg) SerializationUtils.clone(errorMsgCache.get(execDetailKey));
    }

    /**
     * 获取所有任务处理器 (只读)
     */
    public Set<TaskHandler> getAllTaskHandlers() {
        Set<TaskHandler> taskHandlers = new HashSet<>();
        taskHandlerCache.values().forEach(taskHandler -> taskHandlers.add((TaskHandler) SerializationUtils.clone(taskHandler)));
        return taskHandlers;
    }

    /**
     * 获取所有执行详情 (只读)
     */
    public Set<ExecDetail> getAllExecDetails() {
        Set<ExecDetail> execDetails = new HashSet<>();
        execDetailCache.values().forEach(execDetail -> execDetails.add((ExecDetail) SerializationUtils.clone(execDetail)));
        return execDetails;
    }

    /**
     * 获取所有错误信息 (只读)
     */
    public Set<ErrorMsg> getAllErrorMsgs() {
        Set<ErrorMsg> errorMsgSet = new HashSet<>();
        errorMsgCache.values().forEach(errorMsg -> errorMsgSet.add((ErrorMsg) SerializationUtils.clone(errorMsg)));
        return errorMsgSet;
    }

    /**
     * 添加任务处理器
     */
    void addTaskHandler(TaskHandler taskHandler) {
        taskHandlerCache.put(taskHandler.getKey(), taskHandler);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.insertTaskHandler(taskHandler);
    }

    /**
     * 添加任务处理器
     */
    void addExecDetail(ExecDetail execDetail) {
        execDetailCache.put(execDetail.getKey(), execDetail);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.insertExecDetail(execDetail);
    }

    /**
     * 删除任务
     */
    void deleteExecDetail(String execDetailKey) {
        execDetailCache.remove(execDetailKey);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.deleteExecDetail(execDetailKey);
    }

    /**
     * 删除错误信息以及对应的任务详情
     */
    void clearError(String execDetailKey) {
        errorMsgCache.remove(execDetailKey);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.deleteErrorMsg(execDetailKey);

        deleteExecDetail(execDetailKey);
    }

    /**
     * 更新任务状态
     */
    void updateExecState(String execDetailKey, ExecDetail.ExecState execState) {
        ExecDetail execDetail = execDetailCache.get(execDetailKey);
        execDetail.setState(execState);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.updateExecDetail(execDetail);
    }

    /**
     * 更新任务状态为ERROR，并记录错误信息
     */
    void updateExecState2Error(String execDetailKey, String msg) {
        ExecDetail execDetail = execDetailCache.get(execDetailKey);
        execDetail.setState(ExecDetail.ExecState.ERROR);
        ErrorMsg errorMsg = new ErrorMsg(execDetailKey, msg);
        errorMsgCache.put(execDetailKey, errorMsg);

        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.updateExecDetail(execDetail);
        storageService.insertErrorMsg(execDetailKey, msg);
    }

    /**
     * 更新任务的cron表达式
     */
    void updateCron(String execDetailKey, String cron) {
        ExecDetail execDetail = execDetailCache.get(execDetailKey);
        execDetail.setCronExpr(cron);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.updateExecDetail(execDetail);
    }

    /**
     * 更新任务的终止时间
     */
    void updateEndTime(String execDetailKey, LocalDateTime endTime) {
        ExecDetail execDetail = execDetailCache.get(execDetailKey);
        execDetail.setEndTime(endTime);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.updateExecDetail(execDetail);
    }

    /**
     * 更新任务的最大执行次数
     */
    void updateMaxExecCount(String execDetailKey, Long maxExecCount) {
        ExecDetail execDetail = execDetailCache.get(execDetailKey);
        execDetail.setMaxExecCount(maxExecCount);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.updateExecDetail(execDetail);
    }

    /**
     * 更新任务的最近执行时间和执行次数
     */
    void updateExecDetail(String execDetailKey, LocalDateTime lastExecTime, Long execCount) {
        ExecDetail execDetail = execDetailCache.get(execDetailKey);
        execDetail.setLastExecTime(lastExecTime);
        execDetail.setExecCount(execCount);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        storageService.updateExecDetail(execDetail);
    }

}
