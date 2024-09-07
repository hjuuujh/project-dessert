package com.zerobase.storeapi.domain.redis.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteOptionCartForm {
    private List<Long> optionIds;
}
