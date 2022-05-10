package com.jcy.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserLoadSecurityServiceImpl userLoadSecurityService;

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userLoadSecurityService);
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/public/*").hasAnyAuthority("student", "teacher", "admin")
                .mvcMatchers("/student/*").hasAnyAuthority("student", "admin")
                .mvcMatchers("/teacher/*").hasAnyAuthority("teacher", "admin")
                .mvcMatchers("/admin/*").hasAnyAuthority("admin")

                .anyRequest().authenticated()

                .and()

                // token认证
                .addFilterBefore(new TokenAuthFilter(authenticationManager()), BasicAuthenticationFilter.class)

                // 认证异常处理器
                .exceptionHandling()
                .accessDeniedHandler(new MyAccessDeniedHandler())
                .authenticationEntryPoint(new MyAuthenticationEntryPointHandler())

                .and().csrf().disable();// 默认csrf token 校验开启，针对 POST, PUT, PATCH

        http.formLogin().disable();
        // 前后端分离关闭配置登录
        http.logout().disable();

        // 所有的Rest服务一定要设置为无状态，以提升操作性能
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/util/**", "/common/**")
                .antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/v2/**", "/api/**");
    }
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }
}
