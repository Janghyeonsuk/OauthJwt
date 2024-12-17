package com.example.oauthjwt.auth.handler;

import com.example.oauthjwt.auth.jwt.TokenProvider;
import com.example.oauthjwt.util.constants.TokenKey;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private static final String URI = "/api/auth/success";
    private static final String SWAGGER = "http://localhost:8080/login";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("Oauth 성공 핸들러");

        String accessToken = tokenProvider.generateAccessToken(authentication);
        tokenProvider.generateRefreshToken(authentication, accessToken);

        // Authorization 헤더에 Bearer 토큰 설정
        response.setHeader(AUTHORIZATION, TokenKey.TOKEN_PREFIX + accessToken); // "Bearer " 접두어 추가
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 응답 바디에 성공 메시지와 필요한 정보 반환
        response.getWriter().write("{"
                + "\"message\": \"Authentication successful\", "
                + "\"tokenType\": \"Bearer\", "
                + "\"accessToken\": \"" + accessToken + "\""
                + "}");
        response.getWriter().flush();

//        response.setHeader(AUTHORIZATION, TokenKey.TOKEN_PREFIX + accessToken);
//        response.setHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        String redirectUrl = UriComponentsBuilder.fromUriString(URI)
//                .queryParam("accessToken", accessToken)
//                .build().toUriString();
//
//        response.sendRedirect(redirectUrl);
    }
}
