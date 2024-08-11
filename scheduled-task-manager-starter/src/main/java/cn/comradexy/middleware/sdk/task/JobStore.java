package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务存储区
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-10
 * @Description: 任务存储区
 */
public class JobStore {
    private static final Map<String, Job> JOB_MAP = new ConcurrentHashMap<>(64);
    private static final Map<String, ExecDetail> EXEC_DETAIL_MAP = new ConcurrentHashMap<>(64);

    public static void addJob(Job job) {
        JOB_MAP.put(job.getKey(), job);
    }

    public static void addExecDetail(ExecDetail execDetail) {
        EXEC_DETAIL_MAP.put(execDetail.getKey(), execDetail);
    }

    public static void deleteExecDetail(String execDetailKey) {
        EXEC_DETAIL_MAP.remove(execDetailKey);
    }

    public static void deleteJob(String jobKey) {
        Job job =  JOB_MAP.remove(jobKey);
        // 查询 EXEC_DETAIL_MAP 中所有 jobKey==job.key 的 ExecDetail，然后删除
        EXEC_DETAIL_MAP.entrySet().removeIf(entry -> entry.getValue().getJobKey().equals(job.getKey()));
    }

    public static Job getJob(String jobKey) {
        return JOB_MAP.get(jobKey);
    }

    public static ExecDetail getExecDetail(String execDetailKey) {
        return EXEC_DETAIL_MAP.get(execDetailKey);
    }

    public static void setPaused(String execDetailKey) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(ExecDetail.ExecState.PAUSED);
    }

    public static void setRunning(String execDetailKey) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(ExecDetail.ExecState.RUNNING);
    }

    public static void setComplete(String execDetailKey) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(ExecDetail.ExecState.COMPLETE);
    }

    public static void setError(String execDetailKey) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(ExecDetail.ExecState.ERROR);
    }

    public static void setBlocked(String execDetailKey) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(ExecDetail.ExecState.BLOCKED);
    }

    /**
     * 保存任务及执行细节: JDBC/Redis
     */
    public static void save() {
        // TODO: 调用JDBC或者Redis服务保存任务及执行细节

    }
}
