package com.zerobase.storeapi.repository;

import com.zerobase.storeapi.domain.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreItemOptionRepository extends JpaRepository<Option, Long> {
    @Modifying
    @Query(value = "delete from `option` where item_id = (:itemId)", nativeQuery = true)
    void deleteAllByItemId(@Param("itemId") Long itemId);
}
