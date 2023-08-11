package com.api.gateway.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "用户信息")
public class UserDTO {

    @Schema(description = "用户名", defaultValue = "张三")
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @Schema(description = "密码", defaultValue = "123456")
    @NotEmpty(message = "密码不能为空")
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
