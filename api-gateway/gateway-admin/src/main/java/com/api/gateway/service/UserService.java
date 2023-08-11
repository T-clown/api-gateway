package com.api.gateway.service;


import com.api.gateway.model.dto.UserDTO;
import jakarta.servlet.http.HttpServletResponse;


public interface UserService {

    void add(UserDTO userDTO);

    void login(UserDTO userDTO, HttpServletResponse response);

}
