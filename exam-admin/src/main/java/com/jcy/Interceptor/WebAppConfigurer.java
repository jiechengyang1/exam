package com.jcy.Interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author JCY
 * @implNote 2022/02/09 10:57
 */
@Configuration
@RequiredArgsConstructor
public class WebAppConfigurer implements WebMvcConfigurer {

    private final AdminInterceptor adminInterceptor;

    private final TeacherInterceptor teacherInterceptor;

    private final StudentInterceptor studentInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 可添加多个
        // 拦截未登录进入超级管理员的界面
//        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**");
//        registry.addInterceptor(teacherInterceptor).addPathPatterns("/teacher/**");
//        registry.addInterceptor(studentInterceptor).addPathPatterns("/student/**");
    }

}
