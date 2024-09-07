package com.zerobase.storeapi.domain.redis.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddItemCartForm {
    private Long id;
    private Long storeId;
    private String storeName;
    private String name;
    private List<Option> options;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option{
        private Long id;
        private String name;
        private Integer quantity;
        private Integer price;
    }
}
