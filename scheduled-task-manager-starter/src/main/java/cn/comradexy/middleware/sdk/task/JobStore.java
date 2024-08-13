package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;
import cn.comradexy.middleware.sdk.support.storage.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
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
public class JobStore implements IJobStore {
    private final Map<String, Job> JOB_MAP = new ConcurrentHashMap<>(64);
    private final Map<String, ExecDetail> EXEC_DETAIL_MAP = new ConcurrentHashMap<>(64);

    @Override
    public void addJob(Job job) {
        JOB_MAP.put(job.getKey(), job);
    }

    @Override
    public void addExecDetail(ExecDetail execDetail) {
        EXEC_DETAIL_MAP.put(execDetail.getKey(), execDetail);
    }

    @Override
    public void deleteExecDetail(String execDetailKey) {
        EXEC_DETAIL_MAP.remove(execDetailKey);
    }

    @Override
    public void deleteJob(String jobKey) {
        Job job = JOB_MAP.remove(jobKey);
        // 查询 EXEC_DETAIL_MAP 中所有 jobKey==job.key 的 ExecDetail，然后删除
        EXEC_DETAIL_MAP.entrySet().removeIf(entry -> entry.getValue().getJobKey().equals(job.getKey()));
    }

    @Override
    public Job getJob(String jobKey) {
        return JOB_MAP.get(jobKey);
    }

    @Override
    public ExecDetail getExecDetail(String execDetailKey) {
        return EXEC_DETAIL_MAP.get(execDetailKey);
    }

    @Override
    public Set<Job> getAllJobs() {
        return new HashSet<>(JOB_MAP.values());
    }

    @Override
    public Set<ExecDetail> getAllExecDetails() {
        return new HashSet<>(EXEC_DETAIL_MAP.values());
    }

    @Override
    public void updateState(String execDetailKey, ExecDetail.ExecState state) {
        EXEC_DETAIL_MAP.get(execDetailKey).setState(state);
    }

    @Override
    public void save() {
        // TODO: 调用JDBC保存任务及执行细节

    }

    @Override
    public void load() {
        // TODO:

    }
}
