package com.zerobase.orderapi.client;

import com.zerobase.orderapi.client.from.MatchForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "store", url = "${external-api.store.url}")
public interface StoreClient {

    // 요청한 유저의 id 가져옴
    @GetMapping("/match")
    boolean isMatchedStoreAndSeller(@RequestBody MatchForm form);

}
