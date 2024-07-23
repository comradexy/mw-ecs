package cn.comradexy.middleware.sdk.domain.model.valobj;

/**
 * 服务响应状态
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-23
 * @Description: 服务响应状态
 */
public enum ServiceResponseStatus {
    /**
     * 执行成功
     */
    SUCCESS,

    /**
     * 执行失败
     */
    FAILED,

    /**
     * 任务不存在
     */
    NOT_FOUND,

    /**
     * cron表达式错误
     */
    CRON_INVALID
}
