package com.api.gateway.exception;


import com.api.gateway.constants.GatewayCodeEnum;


public final class GatewayException extends RuntimeException {

    private Integer code;

    private String message;

    public GatewayException(GatewayCodeEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMsg();
    }

    public GatewayException(String errMsg) {
        super(errMsg);
        this.message = errMsg;
        this.code = 5000;
    }

    public GatewayException(Integer code, String errMsg) {
        this.code = code;
        this.message = errMsg;
    }


    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
