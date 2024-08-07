package com.example.oauthjwt.auth.service;

import com.example.oauthjwt.auth.exception.TokenException;
import com.example.oauthjwt.global.exception.constant.ErrorCode;
import com.example.oauthjwt.redis.entity.Token;
import com.example.oauthjwt.redis.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void deleteRefreshToken(String userKey) {
        tokenRepository.deleteById(userKey);
    }

    @Transactional
    public void saveOrUpdate(String userKey, String refreshToken, String accessToken) {
        Token token = tokenRepository.findByAccessToken(accessToken)
                .map(o -> o.updateRefreshToken(refreshToken))
                .orElseGet(() -> new Token(userKey, refreshToken, accessToken));

        tokenRepository.save(token);
    }

    public Token findByUserKeyOrThrow(String userKey){
        return tokenRepository.findById(userKey)
                .orElseThrow(() -> new TokenException(ErrorCode.NOT_FOUND_TOKEN));
    }

    public Token findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new TokenException(ErrorCode.TOKEN_EXPIRED));
    }

    public Boolean checkToken(String accessToken) {
        return tokenRepository.existsByAccessToken(accessToken);
    }

    @Transactional
    public void updateToken(String accessToken, Token token) {
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }
}
