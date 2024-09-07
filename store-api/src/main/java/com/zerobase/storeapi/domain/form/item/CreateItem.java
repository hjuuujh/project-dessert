package com.zerobase.storeapi.domain.form.item;

import com.zerobase.storeapi.domain.form.option.CreateOption;
import com.zerobase.storeapi.domain.type.Category;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class CreateItem {
    private Long storeId;
    @NotBlank(message = "아이템명은 필수입니다.")
    private String name;
    private Category category;
    private String thumbnailUrl;
    private String description;
    private String descriptionUrl;
    private List<CreateOption> options;

}
