package com.zerobase.storeapi.client;

import com.zerobase.storeapi.client.from.OrderResult;
import com.zerobase.storeapi.domain.redis.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(value = "order", url = "${external-api.order.url}")
public interface OrderClient {

    @PostMapping
    List<OrderResult> order(@RequestHeader(name = "Authorization") String token,
                            @RequestBody Cart cart);
}
