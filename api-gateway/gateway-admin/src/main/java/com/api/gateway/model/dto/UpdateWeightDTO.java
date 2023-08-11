package com.api.gateway.model.dto;


import jakarta.validation.constraints.NotNull;

public class UpdateWeightDTO {

    @NotNull(message = "实例id不能为空")
    private Integer id;

    @NotNull(message = "权重不能为空")
    private Integer weight;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
