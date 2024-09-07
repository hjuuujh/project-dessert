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
public class ItemDto {
    private Long id;
    private Long storeId;

    private String name;
    private String thumbnailUrl;
    private String description;
    private String descriptionUrl;

    private int price;
    private Category category;

    private List<OptionDto> options;

    private long orderCount;
    private long heartCount;

    public static ItemDto from(Item item) {
        List<OptionDto> options = item.getOptions()
                .stream().map(OptionDto::from).collect(Collectors.toList());
        return ItemDto.builder()
                .id(item.getId())
                .storeId(item.getStoreId())
                .name(item.getName())
                .category(item.getCategory())
                .thumbnailUrl(item.getThumbnailUrl())
                .description(item.getDescription())
                .descriptionUrl(item.getDescriptionUrl())
                .options(options)
                .orderCount(item.getOrderCount())
                .heartCount(item.getHeartCount())
                .price(item.getPrice())
                .build();
    }

}
