package cn.comradexy.middleware.ecs.support.admin.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 响应结果
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 响应结果
 */
@Data
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return new Result<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> Result<T> fail() {
        return new Result<>(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }
}
