package com.zerobase.storeapi.domain.form.item;

import com.zerobase.storeapi.domain.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchItem {
    private Long storeId;
    private String storeName;
    private Long itemId;
    private String  itemName;
    private String thumbnailUrl;
    private int price;

    public static SearchItem from(Item item) {
        return SearchItem.builder()
                .storeId(item.getStoreId())
                .storeName(item.getStoreName())
                .itemId(item.getId())
                .itemName(item.getName())
                .thumbnailUrl(item.getThumbnailUrl())
                .price(item.getPrice())
                .build();
    }
}
