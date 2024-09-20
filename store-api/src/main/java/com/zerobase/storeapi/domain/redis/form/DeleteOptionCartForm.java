package com.zerobase.storeapi.domain.redis.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteOptionCartForm {
    @NotNull(message = "옵션 id는 필수입니다.")
    private List<Long> optionIds;
}
