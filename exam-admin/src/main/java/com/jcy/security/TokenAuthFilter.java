package com.jcy.security;

import com.jcy.exception.BusinessException;
import com.jcy.exception.CommonErrorCode;
import com.jcy.utils.JwtUtils;
import com.jcy.vo.TokenVo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenAuthFilter extends BasicAuthenticationFilter {

    private final Map<Integer, String> roleMap = new ConcurrentHashMap<>(3);

    public TokenAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        roleMap.put(1, "student");
        roleMap.put(2, "teacher");
        roleMap.put(3, "admin");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        TokenVo userInfo = JwtUtils.getUserInfoByToken(request);
        if (userInfo == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(roleMap.get(userInfo.getRoleId())));
        // 配置token认证用户的权限
        UsernamePasswordAuthenticationToken userAuthorization = new UsernamePasswordAuthenticationToken(userInfo.getUsername(), request.getHeader("authorization"), roles);
        SecurityContextHolder.getContext().setAuthentication(userAuthorization);
        // 结束
        chain.doFilter(request, response);
    }
}
