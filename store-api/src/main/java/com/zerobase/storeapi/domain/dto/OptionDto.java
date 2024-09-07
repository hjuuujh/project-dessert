package com.zerobase.storeapi.domain.dto;

import com.zerobase.storeapi.domain.entity.Option;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionDto {
    private Long id;

    private String name;
    private int quantity;
    private int price;

    public static OptionDto from(Option option) {
        return OptionDto.builder()
                .id(option.getId())
                .name(option.getName())
                .quantity(option.getQuantity())
                .price(option.getPrice())
                .build();

    }
}
