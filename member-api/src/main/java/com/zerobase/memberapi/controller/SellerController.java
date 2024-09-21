package com.zerobase.memberapi.controller;

import com.zerobase.memberapi.aop.IncomeLock;
import com.zerobase.memberapi.client.from.DecreaseBalanceForm;
import com.zerobase.memberapi.client.from.IncreaseBalanceForm;
import com.zerobase.memberapi.security.TokenProvider;
import com.zerobase.memberapi.service.CustomerService;
import com.zerobase.memberapi.service.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/seller")
public class SellerController {
    private final CustomerService customerService;
    private final SellerService sellerService;
    private final TokenProvider tokenProvider;

    @GetMapping
    public ResponseEntity<?> getSeller(@RequestHeader(name = "Authorization") String token){
        return ResponseEntity.ok(sellerService.getSeller(tokenProvider.getUserIdFromToken(token)));
    }
    @PostMapping("/income")
    @IncomeLock
    public void income(@RequestHeader(name = "Authorization") String token,
                @RequestBody IncreaseBalanceForm form){
        sellerService.income(tokenProvider.getUserIdFromToken(token), form);
    }

    @PostMapping("/refund")
    @IncomeLock
    public void refund(@RequestHeader(name = "Authorization") String token,
                @RequestBody DecreaseBalanceForm form){
        sellerService.refund(tokenProvider.getUserIdFromToken(token), form);
    }
}