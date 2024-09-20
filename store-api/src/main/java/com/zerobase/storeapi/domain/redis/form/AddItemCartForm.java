package com.zerobase.storeapi.domain.redis.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddItemCartForm {

    @NotNull(message = "아이템 id는 필수입니다.")
    private Long id;
    @NotNull(message = "스토어 id는 필수입니다.")
    private Long storeId;
    @NotBlank(message = "스토어 이름은 필수입니다.")
    private String storeName;
    @NotBlank(message = "아이템 이름은 필수입니다.")
    private String name;
    @NotNull(message = "옵션은 필수입니다.")
    private List<Option> options;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Option{
        @NotNull(message = "옵션 id는 필수입니다.")
        private Long id;
        @NotBlank(message = "옵션명은 필수입니다.")
        private String name;
        @NotNull(message = "수량은 필수입니다.")
        private Integer quantity;
        @NotNull(message = "가격은 필수입니다.")
        private Integer price;
    }
}
