package com.zerobase.orderapi.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelRefundOrder {
    private String itemName;
    private String optionName;
    private LocalDateTime cancelTime;
}
