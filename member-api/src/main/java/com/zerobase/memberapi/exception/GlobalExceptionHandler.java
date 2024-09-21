package com.zerobase.memberapi.exception;

import io.jsonwebtoken.MalformedJwtException;
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

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleSecurityException(SecurityException e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(TOKEN_UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleMalformedJwtException(SecurityException e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(TOKEN_UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleExpiredJwtException(SecurityException e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(TOKEN_UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleUnsupportedJwtException(SecurityException e) {
        log.error("Exception is occurred", e);

        return new ErrorResponse(TOKEN_UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleIllegalArgumentException(SecurityException e) {
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