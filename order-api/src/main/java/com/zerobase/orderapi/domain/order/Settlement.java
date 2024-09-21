package com.zerobase.orderapi.domain.order;

import com.zerobase.orderapi.domain.type.SettlementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private LocalDate date;

    private Long settlementAmount;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status;

    private LocalDateTime createdAt;

    public void decreaseSettlementAmount(Integer totalPrice) {
        this.settlementAmount -= totalPrice;
    }

    public void done() {
        this.status = SettlementStatus.DONE;
    }
}
