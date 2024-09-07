package com.zerobase.storeapi.domain.form.item;

import com.zerobase.storeapi.domain.form.option.CreateOption;
import com.zerobase.storeapi.domain.type.Category;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class UpdateItem {
    private Long id;
    private Long storeId;
    private String name;
    private String thumbnailUrl;
    private String description;
    private String descriptionUrl;
    private Category category;
    private List<CreateOption> options;
}
