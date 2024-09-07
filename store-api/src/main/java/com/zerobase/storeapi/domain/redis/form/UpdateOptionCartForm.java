package com.zerobase.storeapi.domain.redis.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOptionCartForm {
    private Long itemId;
    private Long optionId;
    private Integer quantity;
}
