package com.zerobase.memberapi.client;

import com.zerobase.memberapi.client.from.FollowForm;
import com.zerobase.memberapi.client.from.HeartForm;
import com.zerobase.memberapi.client.from.ItemsForm;
import com.zerobase.memberapi.client.from.StoresForm;
import com.zerobase.memberapi.domain.store.ItemDto;
import com.zerobase.memberapi.domain.store.StoreDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "store", url = "${external-api.store.url}")
public interface StoreClient {
    // store에 팔로우수 증가
    @PostMapping("/follow")
    boolean increaseFollow(@RequestBody FollowForm form);

    @PostMapping("/unfollow")
    boolean decreaseFollow(@RequestBody FollowForm request);

    @PostMapping("/item/heart")
    boolean increaseHeart(@RequestBody HeartForm form);

    @PostMapping("/item/unheart")
    boolean decreaseHeart(@RequestBody HeartForm request);

    @PostMapping("/list")
    Page<StoreDto> getStores(@RequestBody StoresForm request, @SpringQueryMap Pageable pageable);

    @PostMapping("/item/list")
    Page<ItemDto> getItems(@RequestBody ItemsForm request, @SpringQueryMap Pageable pageable);

}
