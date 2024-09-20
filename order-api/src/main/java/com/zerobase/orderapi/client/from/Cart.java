package com.zerobase.orderapi.client.from;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Cart {

    @NotNull(message = "주문 아이템은 필수입니다.")
    private List<Item> items;
    @NotNull(message = "주문 가격은 필수입니다.")
    private int totalPrice;
    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Item  {
        @NotNull(message = "주문 아이템 id는 필수입니다.")
        private Long id;
        @NotNull(message = "주문 아이템의 스토어 id는 필수입니다.")
        private Long storeId;
        @NotNull(message = "주문 아이템의 seller id는 필수입니다.")
        private Long sellerId;
        @NotBlank(message = "주문 매장명은 필수입니다.")
        private String storeName;
        @NotBlank(message = "주문 아이템 명은 필수입니다.")
        private String name;
        @NotNull(message = "주문 아이템의 옵션은 필수입니다.")
        private List<Option> options;

    }

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class Option {
        @NotNull(message = "주문 옵션 id는 필수입니다.")
        private Long id;
        @NotBlank(message = "주문 옵션명은 필수입니다.")
        private String name;
        @NotNull(message = "주문 수량은 필수입니다.")
        private Integer quantity;
        @NotNull(message = "주문 가격은 필수입니다.")
        private Integer price;
    }
}
