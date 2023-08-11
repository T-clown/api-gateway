package com.api.gateway.config;

import com.api.gateway.exception.GatewayException;
import com.api.gateway.model.vo.Result;
import com.api.gateway.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 未知异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handlerBusinessException(Exception e) {
        log.error("未知异常", e);
        return ResultUtil.unknownError(e.getMessage());
    }

    /**
     * 参数异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handlerArgumentException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMsg = getErrorMsg(bindingResult);
        log.error("参数错误:{}", errorMsg);
        return ResultUtil.methodArgumentError(errorMsg);
    }

    /**
     * 参数异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handlerBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMsg = getErrorMsg(bindingResult);
        log.error("参数错误:{}", errorMsg);
        return ResultUtil.methodArgumentError(errorMsg);
    }

    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(GatewayException.class)
    public Result<Void> handlerGatewayException(GatewayException e) {
        log.error("业务异常", e);
        return ResultUtil.error(e);
    }


    private String getErrorMsg(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder sb = new StringBuilder();
        fieldErrors.forEach(fieldError -> {
            sb.append(fieldError.getDefaultMessage());
            sb.append("-");
        });
        return sb.toString();
    }

}
