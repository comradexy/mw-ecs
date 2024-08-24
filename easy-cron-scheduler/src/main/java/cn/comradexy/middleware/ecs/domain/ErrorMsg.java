package cn.comradexy.middleware.ecs.domain;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ErrorMsg implements Serializable {
    private String execDetailKey;
    private String errorMsg;
}
