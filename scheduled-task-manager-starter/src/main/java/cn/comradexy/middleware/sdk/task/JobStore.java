package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.common.ScheduleContext;
import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<String, Job> jobCache = new ConcurrentHashMap<>(64);
    private final Map<String, ExecDetail> execDetailCache = new ConcurrentHashMap<>(64);


    @Override
    public void addJob(Job job) {
        jobCache.put(job.getKey(), job);
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (ScheduleContext.storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        ScheduleContext.storageService.insertJob(job);
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
    public void deleteJob(String jobKey) {
        Job job = jobCache.remove(jobKey);
        // 查询 EXEC_DETAIL_MAP 中所有 jobKey==job.key 的 ExecDetail，然后删除
        execDetailCache.entrySet().removeIf(entry -> entry.getValue().getJobKey().equals(job.getKey()));
        if (!ScheduleContext.properties.getEnableStorage()) return;
        if (ScheduleContext.storageService == null) {
            throw new RuntimeException("存储服务已启用，但 StorageService 未初始化");
        }
        ScheduleContext.storageService.deleteJob(jobKey);
    }

    @Override
    public Job getJob(String jobKey) {
        return jobCache.get(jobKey);
    }

    @Override
    public ExecDetail getExecDetail(String execDetailKey) {
        return execDetailCache.get(execDetailKey);
    }

    @Override
    public Set<Job> getAllJobs() {
        return new HashSet<>(jobCache.values());
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

    @Override
    public void save() {
        // TODO: 调用JDBC保存任务及执行细节

    }

}
