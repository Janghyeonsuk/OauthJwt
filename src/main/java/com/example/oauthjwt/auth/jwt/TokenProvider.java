package com.example.oauthjwt.auth.jwt;

import com.example.oauthjwt.auth.exception.TokenException;
import com.example.oauthjwt.auth.service.TokenService;
import com.example.oauthjwt.global.exception.constant.ErrorCode;
import com.example.oauthjwt.redis.entity.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L; // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60L * 24 * 14; // 2주
    private static final String KEY_ROLE = "role";

    @Value("${spring.jwt.key}")
    private String key;
    private SecretKey secretKey;
    private final TokenService tokenService;

    @PostConstruct
    private void setSecretKey() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    // 1. access token 발급
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // 2. refresh token 발급
    public void generateRefreshToken(Authentication authentication, String accessToken) {
        String refreshToken = generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME);
        tokenService.saveOrUpdate(authentication.getName(), refreshToken, accessToken);
    }

    private String generateToken(Authentication authentication, long expireTime) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());

        return Jwts.builder()
                .subject(authentication.getName())
                .claim(KEY_ROLE, authorities)
                .issuedAt(now) // 만들어진 시간
                .expiration(expiredDate) // 유효 기간
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        // 3. security의 User 객체 생성
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get(KEY_ROLE).toString()));
    }

    // 4. accessToken 재발급
    public String reissueAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            Token token = tokenService.findByAccessTokenOrThrow(accessToken);
            String refreshToken = token.getRefreshToken();

            if (validateToken(refreshToken)) {
                String reissueAccessToken = generateAccessToken(getAuthentication(refreshToken));
                tokenService.updateToken(reissueAccessToken, token);
                return reissueAccessToken;
            }
        } else {
            log.debug("유효한 JWT 토큰이 없습니다.");
        }
        return null;
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new TokenException(ErrorCode.INVALID_TOKEN);
        } catch (SecurityException e) {
            throw new TokenException(ErrorCode.INVALID_JWT_SIGNATURE);
        }
    }

    public Long getExpiration(String token) {
        Claims claims = parseClaims(token);

        Date expiration = claims.getExpiration();
        long now = new Date().getTime();

        return (expiration.getTime() - now);
    }
}
