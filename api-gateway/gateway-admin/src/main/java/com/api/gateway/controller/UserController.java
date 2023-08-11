package com.api.gateway.controller;

import com.api.gateway.constants.AdminConstants;
import com.api.gateway.model.dto.UserDTO;
import com.api.gateway.model.vo.Result;
import com.api.gateway.service.UserService;
import com.api.gateway.utils.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.io.IOException;

@Tag(name = "用户管理")
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "添加用户")
    @ResponseBody
    @PostMapping("")
    public Result<Void> add(@RequestBody @Validated UserDTO userDTO) {
        userService.add(userDTO);
        return ResultUtil.success();
    }


    @PostMapping("/login")
    public void login(@Validated UserDTO userDTO, HttpServletResponse response) throws IOException {
        userService.login(userDTO, response);
        response.sendRedirect("/app/list");
    }

    @GetMapping("/login/page")
    public String loginPage() {
        return "login";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie(AdminConstants.TOKEN_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "login";
    }
}
