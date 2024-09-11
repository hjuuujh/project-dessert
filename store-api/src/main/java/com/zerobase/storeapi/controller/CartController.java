package com.zerobase.storeapi.controller;

import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.domain.redis.Cart;
import com.zerobase.storeapi.domain.redis.form.AddItemCartForm;
import com.zerobase.storeapi.domain.redis.form.DeleteOptionCartForm;
import com.zerobase.storeapi.domain.redis.form.UpdateOptionCartForm;
import com.zerobase.storeapi.service.CartOrderService;
import com.zerobase.storeapi.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final CartOrderService cartOrderService;
    private final MemberClient memberClient;

    @PostMapping
    public ResponseEntity<?> addCart(@RequestHeader(name = "Authorization") String token,
                                     @RequestBody AddItemCartForm form){
        return ResponseEntity.ok(cartService.addCart(memberClient.getMemberId(token), form));
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader(name = "Authorization") String token){
        return ResponseEntity.ok(cartService.getCart(memberClient.getMemberId(token)));
    }

    @PatchMapping
    public ResponseEntity<?> deleteCartOption(@RequestHeader(name = "Authorization") String token,
                                              @RequestBody DeleteOptionCartForm form) {
        return ResponseEntity.ok(cartService.deleteCartOption(memberClient.getMemberId(token), form));
    }

    @PutMapping
    public ResponseEntity<?> updateCartOption(@RequestHeader(name = "Authorization") String token,
                                              @RequestBody UpdateOptionCartForm form) {
        return ResponseEntity.ok(cartService.updateCartOption(memberClient.getMemberId(token), form));
    }

    @PostMapping("/order")
    public ResponseEntity<?> orderCart(@RequestHeader(name = "Authorization") String token,
                                       @RequestBody Cart cart) {
        return ResponseEntity.ok(cartOrderService.orderCart(token, memberClient.getMemberId(token), cart));
    }
}
