package com.zerobase.storeapi.controller;

import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.domain.redis.Cart;
import com.zerobase.storeapi.domain.redis.form.AddItemCartForm;
import com.zerobase.storeapi.domain.redis.form.DeleteOptionCartForm;
import com.zerobase.storeapi.domain.redis.form.UpdateOptionCartForm;
import com.zerobase.storeapi.service.CartOrderService;
import com.zerobase.storeapi.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/store/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final CartOrderService cartOrderService;
    private final MemberClient memberClient;

    @PostMapping
    public ResponseEntity<?> addCart(@RequestHeader(name = "Authorization") String token,
                                     @RequestBody @Validated AddItemCartForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }
        return ResponseEntity.ok(cartService.addCart(memberClient.getMemberId(token), form));
    }

    @GetMapping
    public ResponseEntity<?> getCart(@RequestHeader(name = "Authorization") String token) {
        return ResponseEntity.ok(cartService.getCart(memberClient.getMemberId(token)));
    }

    @PatchMapping
    public ResponseEntity<?> deleteCartOption(@RequestHeader(name = "Authorization") String token,
                                              @RequestBody @Validated DeleteOptionCartForm form
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }
        return ResponseEntity.ok(cartService.deleteCartOption(memberClient.getMemberId(token), form));
    }

    @PutMapping
    public ResponseEntity<?> updateCartOption(@RequestHeader(name = "Authorization") String token,
                                              @RequestBody @Validated UpdateOptionCartForm form
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }
        return ResponseEntity.ok(cartService.updateCartOption(memberClient.getMemberId(token), form));
    }

    @PostMapping("/order")
    public ResponseEntity<?> orderCart(@RequestHeader(name = "Authorization") String token,
                                       @RequestBody @Validated Cart cart, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }
        return ResponseEntity.ok(cartOrderService.orderCart(token, memberClient.getMemberId(token), cart));
    }
}
