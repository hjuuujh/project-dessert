package com.zerobase.orderapi.client.from;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchForm {
    private Long sellerId;
    private Long storeId;
}
