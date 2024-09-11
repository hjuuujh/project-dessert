package com.zerobase.orderapi.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSettleDto {
    private Long sellerId;

    private Long settlementAmount;

}
