package com.zerobase.orderapi.controller;

import com.zerobase.orderapi.client.MemberClient;
import com.zerobase.orderapi.client.from.Cart;
import com.zerobase.orderapi.domain.form.RefundForm;
import com.zerobase.orderapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberClient memberClient;

    @PostMapping
    public ResponseEntity<?> order(@RequestHeader(name = "Authorization") String token,
                                   @RequestBody Cart cart) {

        return ResponseEntity.ok(orderService.order(token, cart));
    }


    // 주문 디테일
    @GetMapping("/customer/order")
    public ResponseEntity<?> getOrder(@RequestHeader(name = "Authorization") String token,
                                      @RequestParam Long id){
        return ResponseEntity.ok(orderService.getOrderById(memberClient.getMemberId(token), id));
    }


    // 고객이 주문현황 기간별 확인
    @GetMapping("/customer/orders")
    public ResponseEntity<?> getOrders(@RequestHeader(name = "Authorization") String token,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                                       Pageable pageable){
        return ResponseEntity.ok(orderService.getOrders(memberClient.getMemberId(token), start, end, pageable));
    }

    // 고객이 주문 취소
    @DeleteMapping("/customer/order/cancel")
    public ResponseEntity<?> cancelOrder(@RequestHeader(name = "Authorization") String token,
                                         @RequestParam Long id){
        return ResponseEntity.ok(orderService.cancelOrder(memberClient.getMemberId(token), id));
    }


    // 판매자가 주문현황 날짜별 확인
    @GetMapping("/seller/orders")
    public ResponseEntity<?> getOrdersByStore(@RequestHeader(name = "Authorization") String token,
                                              @RequestParam Long storeId,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                                              Pageable pageable){
        return ResponseEntity.ok(orderService.getOrdersByStore(memberClient.getMemberId(token), storeId, start, end, pageable));
    }

    // 환불 신청
    @PatchMapping("/customer/refund/{id}")
    public ResponseEntity<?> requestRefund(@RequestHeader(name = "Authorization") String token,
                                           @PathVariable Long id){
        return ResponseEntity.ok(orderService.requestRefund(memberClient.getMemberId(token), id));
    }

    // 환불 신청 취소
    @PatchMapping("/customer/refund/cancel/{id}")
    public ResponseEntity<?> cancelRequestRefund(@RequestHeader(name = "Authorization") String token,
                                                 @PathVariable Long id){
        return ResponseEntity.ok(orderService.cancelRequestRefund(memberClient.getMemberId(token), id));
    }

    // 환불 수락
    @PatchMapping("/seller/refund/approve")
    public ResponseEntity<?> approveRequestRefund(@RequestHeader(name = "Authorization") String token,
                                                  @RequestBody RefundForm form){
        return ResponseEntity.ok(orderService.approveRequestRefund(token, memberClient.getMemberId(token), form));
    }

    // 환불 거절
    @PatchMapping("/seller/refund/reject/{id}")
    public ResponseEntity<?> rejectRequestRefund(@RequestHeader(name = "Authorization") String token,
                                                 @PathVariable Long id){
        return ResponseEntity.ok(orderService.rejectRequestRefund(memberClient.getMemberId(token), id));
    }

    // 정산 요청
    @PostMapping("/seller/settlement")
    public ResponseEntity<?> requestSettlement(@RequestHeader(name = "Authorization") String token,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        return ResponseEntity.ok(orderService.requestSettlement(token, memberClient.getMemberId(token), start, end));
    }
}
