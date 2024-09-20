package com.zerobase.orderapi.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundForm {
    @NotNull(message = "주문 id는 필수입니다.")
    private Long id;
    @NotNull(message = "주문 날짜는 필수입니다.")
    private LocalDate date;
}
