package com.api.gateway.service.impl;


import com.api.gateway.constants.AdminConstants;
import com.api.gateway.constants.GatewayCodeEnum;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.mapper.UserMapper;
import com.api.gateway.model.dto.PayLoad;
import com.api.gateway.model.dto.UserDTO;
import com.api.gateway.model.entity.User;
import com.api.gateway.service.UserService;
import com.api.gateway.util.JwtUtils;
import com.api.gateway.utils.StringTools;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;


@Service
public class UserServiceImpl implements UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Value("${gateway.user-password-salt}")
    private String salt;

    public static void main(String[] args) {
        System.out.println(StringTools.md5Digest("admin", "d5ec0a02"));
    }

    @Override
    public void add(UserDTO userDTO) {
        User oldOne = queryByName(userDTO.getUserName());
        if (oldOne != null) {
            throw new GatewayException("the userName already exist");
        }
        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setPassword(StringTools.md5Digest(userDTO.getPassword(), salt));
        user.setCreatedTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public void login(UserDTO userDTO, HttpServletResponse response) {
        User user = queryByName(userDTO.getUserName());
        if (user == null) {
            throw new GatewayException(GatewayCodeEnum.LOGIN_ERROR);
        }
        String pwd = StringTools.md5Digest(userDTO.getPassword(), salt);
        if (!pwd.equals(user.getPassword())) {
            throw new GatewayException(GatewayCodeEnum.LOGIN_ERROR);
        }
        PayLoad payLoad = new PayLoad(user.getId(), user.getUserName());
        try {
            String token = JwtUtils.generateToken(payLoad);
            Cookie cookie = new Cookie(AdminConstants.TOKEN_NAME, token);
            cookie.setHttpOnly(true);
            // 30min
            cookie.setMaxAge(30 * 60);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            LOGGER.error("login error", e);
        }
    }

    private User queryByName(String userName) {
        QueryWrapper<User> wrapper = Wrappers.query();
        wrapper.lambda().eq(User::getUserName, userName);
        return userMapper.selectOne(wrapper);
    }

}
