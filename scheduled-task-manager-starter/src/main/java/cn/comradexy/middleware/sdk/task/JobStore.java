package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;

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
public class JobStore implements IJobStore {
//    @Resource
//    private IStorageService storageService;

    private final Map<String, Job> JOB_MAP = new ConcurrentHashMap<>(64);
    private final Map<String, ExecDetail> EXEC_DETAIL_MAP = new ConcurrentHashMap<>(64);

    public void addJob(Job job) {
        JOB_MAP.put(job.getKey(), job);
    }

    public void addExecDetail(ExecDetail execDetail) {
        EXEC_DETAIL_MAP.put(execDetail.getKey(), execDetail);
    }

    public void deleteExecDetail(String execDetailKey) {
        EXEC_DETAIL_MAP.remove(execDetailKey);
    }

    public void deleteJob(String jobKey) {
        Job job = JOB_MAP.remove(jobKey);
        // 查询 EXEC_DETAIL_MAP 中所有 jobKey==job.key 的 ExecDetail，然后删除
        EXEC_DETAIL_MAP.entrySet().removeIf(entry -> entry.getValue().getJobKey().equals(job.getKey()));
    }

    public Job getJob(String jobKey) {
        return JOB_MAP.get(jobKey);
    }

    public ExecDetail getExecDetail(String execDetailKey) {
        return EXEC_DETAIL_MAP.get(execDetailKey);
    }

    public void setPaused(String execDetailKey) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(ExecDetail.ExecState.PAUSED);
    }

    public void setRunning(String execDetailKey) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(ExecDetail.ExecState.RUNNING);
    }

    public void setComplete(String execDetailKey) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(ExecDetail.ExecState.COMPLETE);
    }

    public void setError(String execDetailKey) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(ExecDetail.ExecState.ERROR);
    }

    public void setBlocked(String execDetailKey) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(ExecDetail.ExecState.BLOCKED);
    }

    /**
     * 保存任务及执行细节
     */
    public static void save() {
        // TODO: 调用JDBC保存任务及执行细节

    }

    /**
     * 加载任务及执行细节
     */
    public static void load() {
        // TODO:

    }
}
