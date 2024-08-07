package com.example.oauthjwt.auth.controller;

import com.example.oauthjwt.auth.dto.LoginResponse;
import com.example.oauthjwt.auth.jwt.TokenProvider;
import com.example.oauthjwt.auth.service.TokenService;
import com.example.oauthjwt.auth.utils.SecurityUtil;
import com.example.oauthjwt.domain.user.application.UserService;
import com.example.oauthjwt.domain.user.dto.UserDto;
import com.example.oauthjwt.redis.entity.Token;
import com.example.oauthjwt.util.constants.TokenKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증 API")
@RequestMapping("/api/auth")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final TokenService tokenService;

    @Operation(summary = "로그인 성공", description = "로그인 성공후 헤더에 토큰 반환한다.")
    @GetMapping("/success")
    public ResponseEntity<LoginResponse> loginSuccess(@Valid LoginResponse loginResponse, HttpServletResponse response) {
        // 응답 헤더에 액세스 토큰 추가
        response.setHeader(AUTHORIZATION, TokenKey.TOKEN_PREFIX + loginResponse.accessToken());
        response.setHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        log.info("로그인에 성공하였습니다.");
        log.info("accessToken : {}", loginResponse.accessToken());

        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "토큰 유효기간", description = "토큰에 남은 유효기간을 반환한다.")
    @GetMapping("/expire")
    public ResponseEntity<Long> getTokenExpireTime(Authentication authentication) {
        String userKey = SecurityUtil.getCurrentName(authentication);
        Token token = tokenService.findByUserKeyOrThrow(userKey);

        return ResponseEntity.ok().body(tokenProvider.getExpiration(token.getAccessToken()));
    }

    @Operation(summary = "로그인", description = "로그인한 유저의 정보를 보여준다.")
    @GetMapping("/login")
    public ResponseEntity<UserDto> login(Authentication authentication) {
        return ResponseEntity.ok(userService.userInfo(SecurityUtil.getCurrentName(authentication)));
    }

    @Operation(summary = "로그아웃", description = "로그아웃해 토큰 정보를 삭제한디.")
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        String userKey = SecurityUtil.getCurrentName(authentication);
        tokenService.deleteRefreshToken(userKey);

        return ResponseEntity.ok().body(userService.userInfo(userKey).getName() + "님의 계정이 로그아웃 되었습니다.");
    }
}

