package com.example.oauthjwt.domain.user.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    ANYONE("ROLE_ANYONE")
    ;

    private final String key;
}

