package com.zerobase.storeapi.repository;

import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.type.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreItemRepository extends JpaRepository<Item, Long> {
    boolean existsByStoreIdAndName(Long storeId, String name);

    boolean existsByStoreIdAndNameAndIdNot(Long storeId, String name, Long id);

    Page<Item> findByNameContainingIgnoreCaseOrderByOrderCountDesc(String name, Pageable pageable);
    Page<Item> findByNameContainingIgnoreCaseOrderByModifiedAtDesc(String name, Pageable pageable);
    Page<Item> findByNameContainingIgnoreCaseOrderByPrice(String name, Pageable pageable);
    Page<Item> findByNameContainingIgnoreCaseOrderByPriceDesc(String name, Pageable pageable);
    Page<Item> findByNameContainingIgnoreCaseAndCategory(String name, Category category, Pageable pageable);

    Page<Item> findByStoreId(Long storeId, Pageable pageable);

    Page<Item> findAllByIdIn(List<Long> ids, Pageable pageable);

    void deleteAllByStoreId(Long storeId);

    List<Item> findAllByIdIn(List<Long> ids);
}
