package com.example.oauthjwt.auth.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(cookie -> cookie.getName().equals(cookieName))
                        .findFirst());
    }

    public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue,
                                 Duration maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setHttpOnly(Boolean.TRUE);
        cookie.setSecure(Boolean.TRUE);
        cookie.setMaxAge((int)maxAge.toSeconds());

        response.addCookie(cookie);
    }

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Optional.of(request.getCookies())
                .ifPresent(cookies ->
                        Arrays.stream(cookies)
                                .filter(cookie -> cookie.getName().equals(cookieName))
                                .forEach(cookie -> {
                                    cookie.setValue(null);
                                    cookie.setPath("/");
                                    cookie.setMaxAge(0);
                                    response.addCookie(cookie);
                                })
                );
    }
}