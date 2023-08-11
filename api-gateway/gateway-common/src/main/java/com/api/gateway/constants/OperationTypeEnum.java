package com.api.gateway.constants;


public enum OperationTypeEnum {
    /**
     * 新增
     */
    INSERT("INSERT","新增"),
    /**
     * 修改
     */
    UPDATE("UPDATE","修改"),
    /**
     * 删除
     */
    DELETE("DELETE","删除");

    private final String code;

    private final String desc;

    OperationTypeEnum(String code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
