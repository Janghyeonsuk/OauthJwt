package com.example.oauthjwt.auth.jwt;

import com.example.oauthjwt.auth.service.TokenService;
import com.example.oauthjwt.util.constants.TokenKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("토큰 인증 필터");

        String jwt = resolveToken(request);
        String requestURI = request.getRequestURI();

        // accessToken 검증
        if (tokenProvider.validateToken(jwt) && tokenService.checkToken(jwt)) {
            log.info("토큰 검증 완료");
            setAuthentication(jwt);
            log.debug("uri : {}", requestURI);
        } else {
            // 만료되었을 경우 accessToken 재발급
            String reissueAccessToken = tokenProvider.reissueAccessToken(jwt);

            if (StringUtils.hasText(reissueAccessToken)) {
                setAuthentication(reissueAccessToken);

                log.info("accessToken {} 재발급 되었습니다.", reissueAccessToken);
                response.setHeader(AUTHORIZATION, TokenKey.TOKEN_PREFIX + reissueAccessToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        if (ObjectUtils.isEmpty(token) || !token.startsWith(TokenKey.TOKEN_PREFIX)) {
            return null;
        }
        return token.substring(TokenKey.TOKEN_PREFIX.length());
    }
}
