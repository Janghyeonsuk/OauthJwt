package com.example.oauthjwt.domain.user.api;

import com.example.oauthjwt.domain.user.application.UserService;
import com.example.oauthjwt.domain.user.dto.UserDto;
import com.example.oauthjwt.domain.user.dto.UserEditRequest;
import com.example.oauthjwt.domain.user.entity.Role;
import com.example.oauthjwt.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "유저", description = "유저 관련 Api (#7)")
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @Operation(summary = "유저 정보", description = "유저의 정보를 받아오기.")
    @GetMapping("/user")
    public ResponseEntity<UserDto> userInfo(
            @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(userService.userInfo(userDetails.getUsername()), HttpStatusCode.valueOf(200));
    }


    @Operation(summary = "유저 수정", description = "유저의 전화번호를 수정한다.")
    @PatchMapping("/user")
    public ResponseEntity<UserDto> userEdit(
            @RequestBody @Valid UserEditRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.userEdit(request, userDetails.getUsername()));
    }

    @Operation(summary = "관리자 설정", description = "유저를 관리자로 설정한다.")
    @PatchMapping("/user/role")
    public ResponseEntity<Role> changeAdmin(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.changeAdmin(userDetails.getUsername()));
    }

    @Operation(summary = "유저 리스트(관리자)", description = "유저의 리스트를 반환한다.")
    @GetMapping("/admin/list")
    public ResponseEntity<List<UserDto>> getList(@AuthenticationPrincipal UserDetails userDetails) {
        userDetails.getUsername();
        return ResponseEntity.ok(userService.getList());
    }

    @Operation(summary = "전체 유저 정보 불러오기", description = "유저의 정보 반환한다.")
    @GetMapping("/user/test")
    public ResponseEntity<User> getMyUserInfo() {
        return ResponseEntity.of(userService.getMyUserWithAuthorities());
    }

    @Operation(summary = "관리자 테스트", description = "유저의 정보를 반환한다.")
    @GetMapping("/admin/{username}")
    public ResponseEntity<User> getMyUserInfo(@PathVariable(name = "username") String userkey) {
        return ResponseEntity.of(userService.getUserWithAuthorities(userkey));
    }

}
