package com.zerobase.storeapi.domain.redis.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOptionCartForm {
    @NotNull(message = "아이템 id는 필수입니다.")
    private Long itemId;
    @NotNull(message = "옵션 id는 필수입니다.")
    private Long optionId;
    @NotNull(message = "수량은 필수입니다.")
    private Integer quantity;
}
