package cn.comradexy.middleware.sdk.common;

import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;

import java.util.Map;

/**
 * 定时任务上下文信息
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 定时任务上下文信息
 */
public class ScheduleContext {
    public static final Map<String, Job> JOB_MAP = new java.util.concurrent.ConcurrentHashMap<>();
    public static final Map<String, ExecDetail> EXEC_DETAIL_MAP = new java.util.concurrent.ConcurrentHashMap<>();
}
