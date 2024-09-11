package com.zerobase.orderapi.domain.order;

import com.zerobase.orderapi.domain.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersDto {
    private Long id;

    private Long sellerId;
    private Long customerId;
    private Long storeId;

    private Long itemId;
    private String  itemName;
    private Long optionId;
    private String  optionName;
    private Integer price;
    private Integer quantity;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrdersDto from(Orders order) {
        return OrdersDto.builder()
                .id(order.getId())
//                .sellerId(order.getSellerId())
                .customerId(order.getCustomerId())
                .storeId(order.getStoreId())
                .itemId(order.getItemId())
                .itemName(order.getItemName())
                .optionId(order.getOptionId())
                .optionName(order.getOptionName())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .modifiedAt(order.getModifiedAt())
                .build();
    }
}


