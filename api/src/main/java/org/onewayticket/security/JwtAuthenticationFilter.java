package org.onewayticket.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {
    // Jwt 인증이 필요 없는 path 목록
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/api/v1/auth",
            "/api/v1/bookings/guest",
            "/api/v1/flights"
    );

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestUri = httpRequest.getRequestURI();

        // 인증이 필요 없는 경로인지 확인
        if (EXCLUDED_PATHS.stream().anyMatch(requestUri::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsername(token);
                httpRequest.setAttribute("username", username);
            }
        }

        chain.doFilter(request, response);
    }
}
