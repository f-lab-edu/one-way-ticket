package org.onewayticket.config;

import lombok.RequiredArgsConstructor;
import org.onewayticket.security.JwtAuthenticationFilter;
import org.onewayticket.security.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {
    private final JwtUtil jwtUtil;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthenticationFilter(jwtUtil));
        registrationBean.addUrlPatterns("/api/v1/*"); // /api/v1/* 경로에만 필터 적용
        registrationBean.setOrder(1); // 필터 우선순위 설정
        return registrationBean;
    }
}
