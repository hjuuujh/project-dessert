package com.zerobase.storeapi.domain.dto;

import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.type.Category;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemSearchDto {
    private Long id;

    private String name;
    private String thumbnailUrl;
    private String description;
    private int price;
    private Category category;

    private long heartCount;

    public static ItemSearchDto from(Item item) {
      return ItemSearchDto.builder()
                .id(item.getId())
                .name(item.getName())
                .category(item.getCategory())
                .thumbnailUrl(item.getThumbnailUrl())
                .description(item.getDescription())
                .heartCount(item.getHeartCount())
                .price(item.getPrice())
                .build();
    }

}
