package com.zerobase.orderapi.repository;

import com.zerobase.orderapi.domain.order.Settlement;
import com.zerobase.orderapi.domain.type.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findBySellerIdAndDate(Long sellerId, LocalDate date);
    List<Settlement> findBySellerIdAndStatusAndDateBetween(Long sellerId, SettlementStatus status, LocalDate startDate, LocalDate endDate);
}
