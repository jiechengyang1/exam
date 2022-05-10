package com.jcy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcy.exception.CommonErrorCode;
import com.jcy.exception.RestErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        RestErrorResponse apiError = new RestErrorResponse(String.valueOf(CommonErrorCode.UNAUTHORIZED.getCode()), authenticationException.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}
