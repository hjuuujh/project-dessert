package com.zerobase.storeapi.domain.form.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteItem {
    private String itemName;
    private LocalDateTime deletedAt;
}
