package com.zerobase.orderapi.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),


    NOT_FOUND_SELLER(HttpStatus.BAD_REQUEST, "파트너 정보가 존재하지 않습니다."),

    UNMATCHED_SELLER_STORE(HttpStatus.BAD_REQUEST, "매장 정보와 파트너 정보가 일치하지 않습니다."),
    UNMATCHED_MEMBER_ORDER(HttpStatus.BAD_REQUEST, "주문 정보와 고객 정보가 일치하지 않습니다."),

    ALREADY_REFUND_REQUEST(HttpStatus.BAD_REQUEST, "환불 신청내역이 존재합니다."),
    NO_REFUND_REQUEST(HttpStatus.BAD_REQUEST, "환불 신청내역이 존재하지않습니다."),
    ALREADY_REFUND_APPROVED(HttpStatus.BAD_REQUEST, "환불 완료된 주문입니다."),
    ALREADY_REFUND_REJECTED(HttpStatus.BAD_REQUEST, "환붐 거절된 주문입니다."),
    NOT_FOUND_CUSTOMER(HttpStatus.BAD_REQUEST, "고객정보가 존재하지 않습니다.");
   private final HttpStatus httpStatus;
    private final String description;
}