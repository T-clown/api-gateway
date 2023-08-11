package com.api.gateway.interceptor;

import com.api.gateway.constants.AdminConstants;
import com.api.gateway.constants.GatewayCodeEnum;
import com.api.gateway.exception.GatewayException;
import com.api.gateway.model.dto.PayLoad;
import com.api.gateway.util.JwtUtils;
import jakarta.servlet.http.Cookie;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.sendRedirect("/user/login/page");
            return false;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(AdminConstants.TOKEN_NAME)) {
                token = cookie.getValue();
            }
        }
        if (StringUtils.isBlank(token)) {
            response.sendRedirect("/user/login/page");
            return false;
        }
        boolean result = JwtUtils.checkSignature(token);
        if (!result) {
            throw new GatewayException(GatewayCodeEnum.TOKEN_ERROR);
        }
        PayLoad payLoad = JwtUtils.getPayLoad(token);
        request.setAttribute("currUser", payLoad.getName());
        return true;
    }

    @Override
    public void postHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
