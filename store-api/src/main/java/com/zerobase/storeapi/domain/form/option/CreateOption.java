package com.zerobase.storeapi.domain.form.option;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class CreateOption {
    @NotBlank(message = "옵션명은 필수입니다.")
    private String name;
    @NotNull(message = "수량은 필수입니다.")
    private int quantity;
    @NotNull(message = "가격은 필수입니다.")
    private int price;
}
