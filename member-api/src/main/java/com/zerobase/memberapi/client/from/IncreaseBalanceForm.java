package com.zerobase.memberapi.client.from;

import com.zerobase.memberapi.aop.BalanceLockIdInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncreaseBalanceForm implements BalanceLockIdInterface {
    private Long customerId;
    private Integer totalPrice;
}
