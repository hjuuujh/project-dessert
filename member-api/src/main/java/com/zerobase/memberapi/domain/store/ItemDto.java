package com.zerobase.memberapi.domain.store;

import lombok.*;

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


    private float rating;
    private long heartCount;
}
