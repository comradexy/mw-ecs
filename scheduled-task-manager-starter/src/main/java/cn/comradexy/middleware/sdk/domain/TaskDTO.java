package cn.comradexy.middleware.sdk.domain;

import lombok.Data;

import java.util.Date;

/**
 * 任务数据传输对象
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 任务数据传输对象
 */
@Data
public class TaskDTO {
    private String key;
    private String desc;
    private String cronExpr;
    private String jobKey;
    private Date initTime;
    private Date endTime;
    private Date lastExecTime;
    private Long execCount;
    private String state;

    public static TaskDTO createTaskDTO(ExecDetail execDetail){
        TaskDTO task = new TaskDTO();
        task.setKey(execDetail.getKey());
        task.setDesc(execDetail.getDesc());
        task.setCronExpr(execDetail.getCronExpr());
        task.setJobKey(execDetail.getJobKey());
        task.setInitTime(execDetail.getInitTime());
        task.setEndTime(execDetail.getEndTime());
        task.setLastExecTime(execDetail.getLastExecTime());
        task.setExecCount(execDetail.getExecCount());
        task.setState(execDetail.getState().getDesc());
        return task;
    }
}
