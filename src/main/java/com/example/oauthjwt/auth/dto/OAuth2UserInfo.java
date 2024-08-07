package com.example.oauthjwt.auth.dto;

import com.example.oauthjwt.domain.user.entity.Role;
import com.example.oauthjwt.domain.user.entity.User;
import com.example.oauthjwt.domain.user.exception.UserException;
import com.example.oauthjwt.global.exception.constant.ErrorCode;
import com.example.oauthjwt.auth.utils.KeyGenerator;
import lombok.Builder;

import java.util.Map;

@Builder
public record OAuth2UserInfo(
        String username,
        String email,
        String profile
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new UserException(ErrorCode.NOT_FOUND_USER);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .username((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        return OAuth2UserInfo.builder()
                .username((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .profile((String) profile.get("profile_image_url"))
                .build();
    }

    private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2UserInfo.builder()
                .username((String) response.get("name"))
                .email((String) response.get("email"))
                .profile((String) response.get("profile_image"))
                .build();
    }

    public User toEntity() {
        return User.builder()
                .username(username)
                .email(email)
                .profile(profile)
                .userKey(KeyGenerator.generateKey())
                .role(Role.USER)
                .build();
    }
}
