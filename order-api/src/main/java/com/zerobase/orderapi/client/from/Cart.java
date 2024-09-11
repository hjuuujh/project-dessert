package com.zerobase.orderapi.client.from;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Cart {

    private Long customerId;
    private List<Item> items;
    private int totalPrice;
    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Item  {

        private Long id;
        private Long storeId;
        private Long sellerId;
        private String storeName;
        private String name;
        private List<Option> options;

    }

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Option {
        private Long id;
        private String name;
        private Integer quantity;
        private Integer price;
    }
}
