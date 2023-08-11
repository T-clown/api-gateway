package com.api.gateway.constants;


import com.api.gateway.exception.GatewayException;

import java.util.Arrays;
import java.util.Objects;

public enum MatchObjectEnum {
    /**
     * DEFAULT
     */
    DEFAULT("DEFAULT", "默认"),
    /**
     * QUERY
     */
    QUERY("QUERY", "参数"),
    /**
     * HEADER
     */
    HEADER("HEADER", "头部");

    private String code;

    private String desc;

    MatchObjectEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MatchObjectEnum fromCode(String code) {
        return Arrays.stream(MatchObjectEnum.values())
                .filter(x -> Objects.equals(x.getCode(), code)).findFirst().orElseThrow(() -> new GatewayException(""));
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
