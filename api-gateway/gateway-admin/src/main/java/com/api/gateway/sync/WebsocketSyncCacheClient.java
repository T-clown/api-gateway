package com.api.gateway.sync;
import com.api.gateway.constants.GatewayCodeEnum;
import com.api.gateway.constants.OperationTypeEnum;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.model.dto.AppRuleDTO;
import com.api.gateway.model.dto.RouteRuleOperationDTO;
import com.api.gateway.service.RuleService;
import com.api.gateway.utils.GatewayThreadFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


//@Component
public class WebsocketSyncCacheClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebsocketSyncCacheClient.class);

    private WebSocketClient client;

    private RuleService ruleService;

    private Gson gson = new GsonBuilder().create();

    public WebsocketSyncCacheClient(@Value("${gateway.server-web-socket-url}") String serverWebSocketUrl,
                                    RuleService ruleService) {
        if (StringUtils.isEmpty(serverWebSocketUrl)) {
            throw new GatewayException(GatewayCodeEnum.CONFIG_ERROR);
        }
        this.ruleService = ruleService;
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1,
                new GatewayThreadFactory("websocket-connect", true).create());
        try {
            client = new WebSocketClient(new URI(serverWebSocketUrl)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    LOGGER.info("client is open");
                    List<AppRuleDTO> list = ruleService.getEnabledRule();
                    String msg = gson.toJson(new RouteRuleOperationDTO(OperationTypeEnum.INSERT, list));
                    send(msg);
                }

                @Override
                public void onMessage(String s) {
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                }

                @Override
                public void onError(Exception e) {
                    LOGGER.error("websocket client error", e);
                }
            };

            client.connectBlocking();
            //使用调度线程池进行断线重连，30秒进行一次
            executor.scheduleAtFixedRate(() -> {
                if (client != null && client.isClosed()) {
                    try {
                        client.reconnectBlocking();
                    } catch (InterruptedException e) {
                        LOGGER.error("reconnect server fail", e);
                    }
                }
            }, 10, 30, TimeUnit.SECONDS);

        } catch (Exception e) {
            LOGGER.error("websocket sync cache exception", e);
            throw new GatewayException(e.getMessage());
        }
    }

    public <T> void send(T t) {
        while (!client.getReadyState().equals(ReadyState.OPEN)) {
            throw new GatewayException("Please check if the gateway-server is started！");
        }
        client.send(gson.toJson(t));
    }
}
