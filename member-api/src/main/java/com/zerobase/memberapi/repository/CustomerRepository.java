package com.zerobase.memberapi.repository;

import com.zerobase.memberapi.domain.member.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    Optional<Customer> findByEmail(String email);
    @Query(value = "select l.follow_list from customer_follow_list l where l.customer_id = (:customerId)", nativeQuery = true)
    List<Long> findFollowList(@Param("customerId") Long customerId);

    @Query(value = "select l.heart_list from customer_heart_list l where l.customer_id = (:customerId)", nativeQuery = true)
    List<Long> findHeartList(@Param("customerId") Long customerId);

    @Modifying
    @Query(value = "delete from customer_follow_list l where l.follow_list = (:storeId)", nativeQuery = true)
    void deleteFollow(@Param("storeId") Long storeId);

    @Modifying
    @Query(value = "delete from customer_heart_list l where l.heart_list = (:itemId)", nativeQuery = true)
    void deleteHeart(@Param("itemId") Long itemId);

    @Query(value = "select count(*) from customer_follow_list l where l.follow_list = (:storeId) AND l.customer_id = (:customerId)", nativeQuery = true)
    int existsFollow(@Param("storeId") Long storeId, @Param("customerId") Long customerId);

    @Query(value = "select count(*) from customer_heart_list l where l.heart_list = (:itemId) AND l.customer_id = (:customerId)", nativeQuery = true)
    int existsHeart(@Param("itemId") Long itemId, @Param("customerId") Long customerId);

}