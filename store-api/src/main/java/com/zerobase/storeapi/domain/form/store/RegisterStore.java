package com.zerobase.storeapi.domain.form.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterStore {
    @NotBlank(message = "매장 이름은 필수입니다.")
    private String name;
    private String description;
    private String thumbnailUrl;
}
