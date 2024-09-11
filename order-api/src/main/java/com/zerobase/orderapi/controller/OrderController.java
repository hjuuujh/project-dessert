package com.zerobase.orderapi.controller;

import com.zerobase.orderapi.client.MemberClient;
import com.zerobase.orderapi.client.from.Cart;
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

}
