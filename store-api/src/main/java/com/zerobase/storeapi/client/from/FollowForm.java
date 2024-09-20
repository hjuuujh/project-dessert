package com.zerobase.storeapi.client.from;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class FollowForm {

    @NotNull(message = "매장 아이디는 필수입니다.")
    private Long storeId;
}
