package com.example.oauthjwt.auth.utils;

import com.example.oauthjwt.global.exception.BusinessException;
import com.example.oauthjwt.global.exception.constant.ErrorCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
@NoArgsConstructor
public class SecurityUtil {
    public static String getCurrentName(Authentication authentication) {
        if (!authentication.isAuthenticated())
            throw new BusinessException(ErrorCode.DO_NOT_LOGIN);

        UserDetails oAuth2User = (UserDetails) authentication.getPrincipal();

        return oAuth2User.getUsername();
    }

    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("SecurityContext에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }
        return Optional.ofNullable(username);
    }
}