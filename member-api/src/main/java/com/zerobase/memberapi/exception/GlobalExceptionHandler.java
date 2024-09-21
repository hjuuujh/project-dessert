package com.zerobase.memberapi.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.zerobase.memberapi.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.zerobase.memberapi.exception.ErrorCode.TOKEN_UNAUTHORIZED;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 에러를 발생시키지 않고 응답 메시지로 클라이언트에게 전달
    @ExceptionHandler(MemberException.class)
    public ErrorResponse handleMemberException(MemberException e) {
        errorLog(e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ErrorResponse handleSecurityException(SecurityException e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(TOKEN_UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ErrorResponse handleMalformedJwtException(MalformedJwtException e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(TOKEN_UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ErrorResponse handleExpiredJwtException(ExpiredJwtException e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(TOKEN_UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ErrorResponse handleUnsupportedJwtException(UnsupportedJwtException e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(TOKEN_UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(TOKEN_UNAUTHORIZED, e.getMessage());
    }


    // 그외 에러
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private static void errorLog(ErrorCode e) {
        log.error("{} is occurred", e);
    }
}