package com.example.oauthjwt.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DisableSessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 세션 강제 비활성화
        httpRequest.getSession(false); // 세션이 없으면 새로 생성하지 않음
        chain.doFilter(request, response);
    }
}
