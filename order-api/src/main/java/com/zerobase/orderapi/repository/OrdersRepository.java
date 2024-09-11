package com.zerobase.orderapi.repository;

import com.zerobase.orderapi.domain.order.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Page<Orders> findAllByCustomerIdAndModifiedAtBetween(Long customerId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Orders> findAllByStoreIdAndModifiedAtBetween(Long storeId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Optional<Orders> findByIdAndSellerId(Long id, Long sellerId);
    Optional<Orders> findByIdAndCustomerId(Long id, Long customerId);
}
