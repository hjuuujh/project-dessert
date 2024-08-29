package com.zerobase.memberapi.domain.member.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChargeForm{
    @NotNull(message = "충전 금액은 필수입니다.")
    private int amount;
}
