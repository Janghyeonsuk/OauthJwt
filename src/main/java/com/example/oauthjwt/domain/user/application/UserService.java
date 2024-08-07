package com.example.oauthjwt.domain.user.application;

import com.example.oauthjwt.auth.utils.SecurityUtil;
import com.example.oauthjwt.domain.user.dto.UserEditRequest;
import com.example.oauthjwt.domain.user.dto.UserDto;
import com.example.oauthjwt.domain.user.entity.Role;
import com.example.oauthjwt.domain.user.entity.User;
import com.example.oauthjwt.domain.user.exception.UserException;
import com.example.oauthjwt.domain.user.repository.UserRepository;

import com.example.oauthjwt.global.exception.BusinessException;
import com.example.oauthjwt.global.exception.constant.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserDto userInfo(String userKey) {
        User user = findByUserKeyOrThrow(userKey);
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto userEdit(UserEditRequest request, String userKey) {
        User user = findByUserKeyOrThrow(userKey);
        user.updateUser(request);
        return UserDto.fromEntity(user);
    }

    @Transactional
    public Role changeAdmin(String userKey) {
        User user = findByUserKeyOrThrow(userKey);
        user.changeAdmin();
        return user.getRole();
    }

    public List<UserDto> getList() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .toList();
    }

    public void checkAccess(String userKey, User user) {
        if (!user.getUserKey().equals(userKey)) {
            throw new BusinessException(ErrorCode.NO_ACCESS);
        }
    }

    private User findByUserKeyOrThrow(String userKey) {
        return userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
    }

    public Optional<User> getMyUserWithAuthorities(){
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findByUserKey);
    }

    public Optional<User> getUserWithAuthorities(String userKey){
        return userRepository.findByUserKey(userKey);
    }
}
