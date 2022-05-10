package com.jcy.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcy.exception.CommonErrorCode;
import com.jcy.exception.RestErrorResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(403);
        RestErrorResponse apiError = new RestErrorResponse(String.valueOf(CommonErrorCode.FORBIDDEN.getCode()), accessDeniedException.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}