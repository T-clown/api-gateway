package com.api.gateway.model.vo;

import com.api.gateway.enums.ResultCode;

import java.io.Serializable;

/**
 * 返回结构体
 */
public class Result<T> implements Serializable {

    private int code;

    private String message;

    private T data;

    private Result() {
    }

    public Result(ResultCode resultCode) {
        this(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public Result(ResultCode resultCode, T data) {
        this(resultCode.getCode(), resultCode.getMessage(), data);
    }

    public Result(ResultCode code, String message) {
        this(code.getCode(), code.getMessage() + ":" + message, null);
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
