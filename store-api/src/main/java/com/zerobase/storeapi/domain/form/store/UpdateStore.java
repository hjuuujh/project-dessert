package com.zerobase.storeapi.domain.form.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStore {
    @NotNull(message = "매장 아이디는 필수입니다.")
    private Long id;
    @NotBlank(message = "매장 이름은 필수입니다.")
    private String name;
    @NotBlank(message = "매장 설명은 필수입니다.")
    private String description;
    private String thumbnailUrl;
}
