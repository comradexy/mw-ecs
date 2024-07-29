package cn.comradexy.middleware.sdk.domain.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 定时任务管理器枚举值对象
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-25
 * @Description: 定时任务管理器枚举值对象
 */
@Getter
public class ScheduledTaskMgrEnumVO {
    public static final String DEFAULT_TASK_SCHEDULER_BEAN_NAME = "taskScheduler";

    public static final String DEFAULT_SCHEDULED_TASK_MGR_SERVICE_BEAN_NAME = "scheduledTaskMgr";
}
