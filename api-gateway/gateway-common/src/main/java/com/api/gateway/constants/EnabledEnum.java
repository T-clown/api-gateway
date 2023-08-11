package com.api.gateway.constants;


public enum EnabledEnum {
    /**
     *
     */
    DISABLED((byte)0,"禁用"),

    ENABLE((byte)1,"启用");

    private final Byte code;

    private final String desc;

    EnabledEnum(Byte code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public Byte getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
