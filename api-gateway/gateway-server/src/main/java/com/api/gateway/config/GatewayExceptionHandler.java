package com.api.gateway.config;

import com.api.gateway.chain.ResponseUtil;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.model.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

public class GatewayExceptionHandler implements WebExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        logger.error("gateway server exception msg:{}", throwable.getMessage());
        if (throwable instanceof GatewayException gatewayException) {
            return ResponseUtil.doResponse(serverWebExchange, new ApiResult(gatewayException.getCode(), gatewayException.getMessage()));
        }
        String errorMsg = "system error";
        if (throwable instanceof IllegalArgumentException) {
            errorMsg = throwable.getMessage();
        }
        return ResponseUtil.doResponse(serverWebExchange, new ApiResult(5000, errorMsg));
    }
}
