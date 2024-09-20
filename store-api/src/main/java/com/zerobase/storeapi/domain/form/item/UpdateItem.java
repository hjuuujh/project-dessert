package com.zerobase.storeapi.domain.form.item;

import com.zerobase.storeapi.domain.form.option.CreateOption;
import com.zerobase.storeapi.domain.type.Category;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class UpdateItem {
    @NotNull(message = "아이템 id는 필수입니다.")
    private Long id;
    @NotNull(message = "매장 id는 필수입니다.")
    private Long storeId;
    @NotBlank(message = "아이템명은 필수입니다.")
    private String name;
    @NotNull(message = "카테고리는 필수입니다.")
    private Category category;
    private String thumbnailUrl;
    @NotBlank(message = "상세정보는 필수입니다.")
    private String description;
    @NotBlank(message = "상세정보는 필수입니다.")
    private String descriptionUrl;
    @NotNull(message = "옵션은 필수입니다.")
    private List<CreateOption> options;
}
