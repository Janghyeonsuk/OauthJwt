package com.example.oauthjwt.domain.user.dto;

import com.example.oauthjwt.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String name;
    private String email;
    private String profile;
    private String phoneNumber;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .name(user.getUsername())
                .email(user.getEmail())
                .profile(user.getProfile())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
