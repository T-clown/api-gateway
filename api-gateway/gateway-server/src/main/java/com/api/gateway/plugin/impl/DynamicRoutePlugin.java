package com.api.gateway.plugin.impl;

import com.alibaba.fastjson.JSON;
import com.api.gateway.cache.LoadBalanceFactory;
import com.api.gateway.cache.RouteRuleCache;
import com.api.gateway.cache.ServiceCache;
import com.api.gateway.chain.PluginChain;
import com.api.gateway.chain.ResponseUtil;
import com.api.gateway.config.ServerConfigProperties;
import com.api.gateway.constants.GatewayCodeEnum;
import com.api.gateway.constants.MatchObjectEnum;
import com.api.gateway.constants.PluginEnum;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.model.dto.AppRuleDTO;
import com.api.gateway.model.dto.ServiceInstance;
import com.api.gateway.plugin.AbstractGatewayPlugin;
import com.api.gateway.spi.LoadBalance;
import com.api.gateway.utils.StringTools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class DynamicRoutePlugin extends AbstractGatewayPlugin {

    private final static Logger LOGGER = LoggerFactory.getLogger(DynamicRoutePlugin.class);

    private static WebClient webClient;

    private static final Gson gson = new GsonBuilder().create();

    static {
        HttpClient httpClient = HttpClient.create()
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(3))
                                .addHandlerLast(new WriteTimeoutHandler(3)))
                .option(ChannelOption.TCP_NODELAY, true);

        webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    public DynamicRoutePlugin(ServerConfigProperties properties) {
        super(properties);
    }

    @Override
    public Integer order() {
        return PluginEnum.DYNAMIC_ROUTE.getOrder();
    }

    @Override
    public String name() {
        return PluginEnum.DYNAMIC_ROUTE.getName();
    }

    @Override
    public Mono<Void> execute(ServerWebExchange exchange, PluginChain pluginChain) {
        String appName = pluginChain.getAppName();
        ServiceInstance serviceInstance = chooseInstance(appName, exchange.getRequest());
        LOGGER.info("selected instance is [{}]", gson.toJson(serviceInstance));
        // request service
        String url = buildUrl(exchange, serviceInstance);
        return forward(exchange, url);
    }

    /**
     * exchangeToMono()适用于获取单个响应的场景，返回Mono<ClientResponse>
     * exchangeToFlux()适用于获取多个响应的场景，返回Flux<ClientResponse>
     * forward request to backend service
     * 请求目标服务
     *
     * @param exchange
     * @param url
     * @return
     */
    private Mono<Void> forward(ServerWebExchange exchange, String url) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpMethod method = request.getMethod();

        WebClient.RequestBodySpec requestBodySpec = webClient.method(method).uri(url).headers((headers) -> headers.addAll(request.getHeaders()));

        WebClient.RequestHeadersSpec<?> reqHeadersSpec;
        if (requireHttpBody(method)) {
            reqHeadersSpec = requestBodySpec.body(BodyInserters.fromDataBuffers(request.getBody()));
        } else {
            reqHeadersSpec = requestBodySpec;
        }

        reqHeadersSpec.exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        response.setStatusCode(clientResponse.statusCode());
                        response.getHeaders().putAll(clientResponse.headers().asHttpHeaders());
                        return response.writeWith(clientResponse.bodyToFlux(DataBuffer.class));
                    } else {
                        return clientResponse.createError();
                    }
                }).timeout(Duration.ofMillis(properties.getTimeOutMillis()))
                .onErrorResume(ex -> Mono.defer(() -> {
                            String errorResultJson = "";
                            if (ex instanceof TimeoutException) {
                                errorResultJson = "{\"code\":5001,\"message\":\"network timeout\"}";
                            } else {
                                errorResultJson = "{\"code\":5000,\"message\":\"system error\"}";
                            }
                            return ResponseUtil.doResponse(exchange, errorResultJson);
                        }).then(Mono.empty()));
        ;

        reqHeadersSpec.exchangeToMono(clientResponse -> {
                    response.setStatusCode(clientResponse.statusCode());
                    response.getHeaders().putAll(clientResponse.headers().asHttpHeaders());
                    return response.writeWith(clientResponse.bodyToFlux(DataBuffer.class));
                })
                .onErrorResume(ex -> Mono.defer(() -> {
                    String errorResultJson = "";
                    if (ex instanceof TimeoutException) {
                        errorResultJson = "{\"code\":5001,\"message\":\"network timeout\"}";
                    } else {
                        errorResultJson = "{\"code\":5000,\"message\":\"system error\"}";
                    }
                    return ResponseUtil.doResponse(exchange, errorResultJson);
                }).then(Mono.empty()));

        // nio->callback->nio
        return reqHeadersSpec.exchange().timeout(Duration.ofMillis(properties.getTimeOutMillis()))
                .onErrorResume(ex -> Mono.defer(() -> {
                            String errorResultJson = "";
                            if (ex instanceof TimeoutException) {
                                errorResultJson = "{\"code\":5001,\"message\":\"network timeout\"}";
                            } else {
                                errorResultJson = "{\"code\":5000,\"message\":\"system error\"}";
                            }
                            return ResponseUtil.doResponse(exchange, errorResultJson);
                        })
                        .then(Mono.empty()))
                .flatMap(backendResponse -> {
                    response.setStatusCode(backendResponse.statusCode());
                    response.getHeaders().putAll(backendResponse.headers().asHttpHeaders());
                    return response.writeWith(backendResponse.bodyToFlux(DataBuffer.class));
                });
    }

    /**
     * weather the http method need http body
     *
     * @param method
     * @return
     */
    private boolean requireHttpBody(HttpMethod method) {
        return method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT) || method.equals(HttpMethod.PATCH);
    }

    /**
     * 构建请求路径
     *
     * @param exchange
     * @param serviceInstance
     * @return
     */
    private String buildUrl(ServerWebExchange exchange, ServiceInstance serviceInstance) {
        ServerHttpRequest request = exchange.getRequest();
        String query = request.getURI().getQuery();
        String path = request.getPath().value().replaceFirst("/" + serviceInstance.getAppName(), "");
        String url = "http://" + serviceInstance.getIp() + ":" + serviceInstance.getPort() + "/" + serviceInstance.getAppName() + path;
        if (StringUtils.isNotBlank(query)) {
            url = url + "?" + query;
        }
        LOGGER.info("{},{}", JSON.toJSONString(serviceInstance), url);
        return url;
    }


    /**
     * choose an ServiceInstance according to route rule config and load balancing algorithm
     *
     * @param appName
     * @param request
     * @return
     */
    private ServiceInstance chooseInstance(String appName, ServerHttpRequest request) {
        List<ServiceInstance> serviceInstances = ServiceCache.getAllInstances(appName);
        if (CollectionUtils.isEmpty(serviceInstances)) {
            LOGGER.error("service instance of {} not find", appName);
            throw new GatewayException(GatewayCodeEnum.SERVICE_NOT_FIND);
        }
        String version = matchAppVersion(appName, request);
        if (StringUtils.isBlank(version)) {
            throw new GatewayException("match app version error");
        }
        // filter serviceInstances by version
        List<ServiceInstance> instances = serviceInstances.stream().filter(i -> i.getVersion().equals(version)).collect(Collectors.toList());
        //Select an instance based on the load balancing algorithm
        LoadBalance loadBalance = LoadBalanceFactory.getInstance(properties.getLoadBalance(), appName, version);
        ServiceInstance serviceInstance = loadBalance.select(instances);
        return serviceInstance;
    }


    private String matchAppVersion(String appName, ServerHttpRequest request) {
        List<AppRuleDTO> rules = RouteRuleCache.getRules(appName);
        rules.sort(Comparator.comparing(AppRuleDTO::getPriority).reversed());
        return rules.stream().filter(rule -> match(rule, request)).findAny().map(AppRuleDTO::getVersion).orElse(null);
    }


    private boolean match(AppRuleDTO rule, ServerHttpRequest request) {
        String matchObject = rule.getMatchObject();
        String matchKey = rule.getMatchKey();
        String matchRule = rule.getMatchRule();
        Byte matchMethod = rule.getMatchMethod();

        MatchObjectEnum matchEnum = MatchObjectEnum.fromCode(matchObject);
        //没有default分支自都不匹配会抛出SwitchCaseException
        return switch (matchEnum) {
            case DEFAULT -> true;
            case QUERY -> {
                String param = request.getQueryParams().getFirst(matchKey);
                yield StringTools.match(param, matchMethod, matchRule);
            }
            case HEADER -> {
                HttpHeaders headers = request.getHeaders();
                String headerValue = headers.getFirst(matchKey);
                yield StringTools.match(headerValue, matchMethod, matchRule);
            }
            default -> false;
        };
    }

}
