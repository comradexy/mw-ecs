package cn.comradexy.middleware.ecs.admin.domain;

import cn.comradexy.middleware.ecs.domain.ExecDetail;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务执行细节数据传输对象
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 任务执行细节数据传输对象
 */
@Data
public class ExecDetailDTO {
    private String key;
    private String desc;
    private String cronExpr;
    private String taskHandlerKey;
    private LocalDateTime initTime;
    private LocalDateTime endTime;
    private LocalDateTime lastExecTime;
    private Long execCount;
    private Long maxExecCount;
    private String state;

    public static ExecDetailDTO createExecDetailDTO(ExecDetail execDetail) {
        ExecDetailDTO execDetailDTO = new ExecDetailDTO();
        execDetailDTO.setKey(execDetail.getKey());
        execDetailDTO.setDesc(execDetail.getDesc());
        execDetailDTO.setCronExpr(execDetail.getCronExpr());
        execDetailDTO.setTaskHandlerKey(execDetail.getTaskHandlerKey());
        execDetailDTO.setInitTime(execDetail.getInitTime());
        execDetailDTO.setEndTime(execDetail.getEndTime());
        execDetailDTO.setLastExecTime(execDetail.getLastExecTime());
        execDetailDTO.setExecCount(execDetail.getExecCount());
        execDetailDTO.setMaxExecCount(execDetail.getMaxExecCount());
        execDetailDTO.setState(execDetail.getState().getDesc());
        return execDetailDTO;
    }
}
