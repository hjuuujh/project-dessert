package com.zerobase.memberapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    TOKEN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Token UnAuthorized"),

    // 회원가입
    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다."),

    // 로그인, 유저정보 가져오기,
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "일치하는 회원이 없습니다."),
    LOGIN_CHECK_FAIL(HttpStatus.BAD_REQUEST, "이메일과 패스워드를 확인해주세요."),

    // 잔액 변경,
    CHECK_AMOUNT(HttpStatus.BAD_REQUEST, "충전 금액을 확인해주세요."),

    // Lock
    ACCOUNT_TRANSACTION_LOCK(HttpStatus.BAD_REQUEST,"해당 계좌는 사용 중입니다."),
    TRANSACTION_NOT_FOUND(HttpStatus.BAD_REQUEST,"해당 거래가 없습니다."),
    TRANSACTION_ACCOUNT_UN_MATCH(HttpStatus.BAD_REQUEST,"이 거래는 해당 계좌에서 발생한 거래가 아닙니다."),

    // store
    ALREADY_FOLLOW_STORE(HttpStatus.BAD_REQUEST, "이미 팔로우한 매장입니다."),
    ALREADY_HEART_ITEM(HttpStatus.BAD_REQUEST, "이미 찜한 아이템입니다."),
    NOT_FOUND_STORE(HttpStatus.BAD_REQUEST, "매장 정보가 존재하지 않습니다."),
    NOT_FOUND_ITEM(HttpStatus.BAD_REQUEST, "아이템 정보가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String description;
}