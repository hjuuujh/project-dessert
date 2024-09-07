package com.zerobase.storeapi.repository;

import com.zerobase.storeapi.domain.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);
    Optional<Store> findByIdAndSellerId(Long id, Long sellerId);

    Page<Store> findByNameContainingIgnoreCaseAndDeletedAt(String keyword, LocalDate deletedAt, Pageable pageable);
    Page<Store> findByNameContainingIgnoreCaseAndDeletedAtOrderByFollowCountDesc(String keyword, LocalDate deletedAt, Pageable pageable);

    Page<Store> findAllByIdInAndDeletedAt(List<Long> ids, LocalDate deletedAt, Pageable pageable);
}
