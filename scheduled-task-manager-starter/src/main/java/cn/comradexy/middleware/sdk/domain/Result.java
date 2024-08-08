package cn.comradexy.middleware.sdk.domain;

import cn.comradexy.middleware.sdk.constants.ServiceResponseStatusVO;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 返回结果
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-23
 * @Description: 返回结果
 */
@Setter
@Getter
@RequiredArgsConstructor
public class Result<T> {
    /**
     * 状态
     */
    @NonNull
    private ServiceResponseStatusVO status;

    /**
     * 具体数据
     */
    private T data;

    /**
     * 成功
     */
    public static <T> Result<T> success() {
        return new Result<>(ServiceResponseStatusVO.SUCCESS);
    }

    /**
     * 成功
     *
     * @param data 数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>(ServiceResponseStatusVO.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 失败
     *
     * @param status 状态
     */
    public static <T> Result<T> failed(ServiceResponseStatusVO status) {
        return new Result<>(status);
    }

    /**
     * 失败
     *
     * @param status 状态
     * @param data   数据
     */
    public static <T> Result<T> failed(ServiceResponseStatusVO status, T data) {
        Result<T> result = new Result<>(status);
        result.setData(data);
        return result;
    }

    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return ServiceResponseStatusVO.SUCCESS.equals(status);
    }
}
