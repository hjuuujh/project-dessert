package com.zerobase.memberapi.domain.store;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {
    private Long id;
    private Long sellerId;
    private String name;
    private String description;
    private String thumbnailUrl;

}
