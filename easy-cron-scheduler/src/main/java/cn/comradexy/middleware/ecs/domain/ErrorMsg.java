package cn.comradexy.middleware.ecs.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 错误信息
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-24
 * @Description: 错误信息
 */
@Data
public class ErrorMsg implements Serializable {
    private String execDetailKey;
    private String errorMsg;
}
