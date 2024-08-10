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
    public static final Map<String, Job> JOB_MAP = new ConcurrentHashMap<>(64);
    public static final Map<String, ExecDetail> EXEC_DETAIL_MAP = new ConcurrentHashMap<>(64);

    /**
     * 保存任务及执行细节: JDBC/Redis
     */
    public static void save() {
        // TODO: 调用JDBC或者Redis服务保存任务及执行细节

    }

}
