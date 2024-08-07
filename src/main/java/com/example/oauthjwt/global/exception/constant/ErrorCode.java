package com.example.oauthjwt.global.exception.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    OK(HttpStatus.OK, "정상 처리 되었습니다."),
  
    DO_NOT_LOGIN(HttpStatus.NOT_FOUND, "현재 로그인중이 아닙니다."),

    // auth
    ILLEGAL_REGISTRATION_ID(HttpStatus.NOT_ACCEPTABLE, "illegal registration id"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "올바르지 않은 토큰입니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "잘못된 JWT 시그니처입니다."),

    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    NOT_FOUND_COMPANY(HttpStatus.NOT_FOUND, "업체를 찾을 수 없습니다."),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "결제를 찾을 수 없습니다."),
    NOT_MATCH_AMOUNT(HttpStatus.BAD_REQUEST,"금액이 다릅니다."),
    NOT_FOUND_ITEM(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    NOT_FOUND_ADDRESS(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다."),
    NOT_FOUND_Subscription(HttpStatus.NOT_FOUND, "해당 구독을 찾을 수 없습니다."),
    NOT_FOUND_Subscription_DATE(HttpStatus.NOT_FOUND,"해당 날짜로 된 구독을 찾을수 없습니다."),
    NOT_FOUND_DEFAULT_ADDRESS(HttpStatus.NOT_FOUND, "기본 배송지가 설정되어 있지 않습니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),

    DUPLICATED_USER(HttpStatus.CREATED, "이미 존재하는 회원입니다."),
    DUPLICATED_ADDRESS(HttpStatus.CREATED, "이미 저장되어 있는 배송지입니다."),
    DUPLICATED_DATE(HttpStatus.CREATED, "이미 상품 요청된 날짜입니다."), // 가정이 요청
  
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "정상적인 요청이 아닙니다."),
    NO_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    UNKNOWN(HttpStatus.INTERNAL_SERVER_ERROR, "알수없는 에러 발생."),
    ;
    private final HttpStatus code;
    private final String message;
}
