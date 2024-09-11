package com.zerobase.orderapi.client;

import com.zerobase.orderapi.client.from.DecreaseBalanceForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "member", url = "${external-api.member.url}")
public interface MemberClient {

    // 요청한 유저의 id 가져옴
    @GetMapping("/id")
    Long getMemberId(@RequestHeader(name = "Authorization") String token);

    @PostMapping("/customer/order")
    void decreaseBalance(@RequestHeader(name = "Authorization") String token,
                         @RequestBody DecreaseBalanceForm form);

}
