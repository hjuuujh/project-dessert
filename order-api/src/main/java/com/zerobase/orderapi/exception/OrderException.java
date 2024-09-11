package com.zerobase.orderapi.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderException extends RuntimeException {
    // store 관련 에러발생한 경우

    private ErrorCode errorCode;
    private String errorMessage;

    public OrderException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}