package com.zerobase.storeapi.client.from;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class OrderResult {
    private Long orderId;
    private String itemName;
    private String optionName;
    private Integer optionPrice;
    private Integer optionQuantity;
    private LocalDateTime createdAt;

}
