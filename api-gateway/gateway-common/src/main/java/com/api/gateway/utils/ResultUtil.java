package com.api.gateway.utils;


import com.api.gateway.enums.ResultCode;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.model.vo.Result;

public class ResultUtil {
    private ResultUtil() {
    }

    public static Result<Void> success() {
        return new Result<>(ResultCode.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }


    public static Result<Void> error(ResultCode errorCode) {
        return new Result<>(errorCode);
    }

    public static Result<Void> error(GatewayException exception) {
        return new Result<>(exception.getCode(), exception.getMessage(), null);
    }

    public static Result<Void> error(ResultCode code, String message) {
        return new Result<>(code == null ? ResultCode.FAILURE : code, message);
    }

    public static Result<Void> methodArgumentError(String errorMessage) {
        return new Result<>(ResultCode.INVALID_PARAMETER, ResultCode.INVALID_PARAMETER.getMessage() + ":" + errorMessage);
    }

    public static Result<Void> unknownError(String message) {
        return new Result<>(ResultCode.UNKNOWN_ERROR, message);
    }
}
