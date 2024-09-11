package com.zerobase.orderapi.controller;

import com.zerobase.orderapi.client.MemberClient;
import com.zerobase.orderapi.client.from.Cart;
import com.zerobase.orderapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
