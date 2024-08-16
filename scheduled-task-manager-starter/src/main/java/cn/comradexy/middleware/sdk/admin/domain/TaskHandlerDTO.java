package cn.comradexy.middleware.sdk.admin.domain;

import cn.comradexy.middleware.sdk.domain.TaskHandler;
import lombok.Data;

/**
 * 任务处理器数据传输对象
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 任务处理器数据传输对象
 */
@Data
public class TaskHandlerDTO {
    private String desc;
    private String beanClassName;
    private String beanName;
    private String methodName;

    public static TaskHandlerDTO createTaskHandlerDTO(TaskHandler taskHandler) {
        TaskHandlerDTO taskHandlerDTO = new TaskHandlerDTO();
        taskHandlerDTO.setDesc(taskHandler.getDesc());
        taskHandlerDTO.setBeanClassName(taskHandler.getBeanClassName());
        taskHandlerDTO.setBeanName(taskHandler.getBeanName());
        taskHandlerDTO.setMethodName(taskHandler.getMethodName());
        return taskHandlerDTO;
    }
}
