package com.zerobase.storeapi.service;

import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.domain.dto.StoreDto;
import com.zerobase.storeapi.domain.entity.Store;
import com.zerobase.storeapi.domain.form.store.DeleteStore;
import com.zerobase.storeapi.domain.form.store.RegisterStore;
import com.zerobase.storeapi.domain.form.store.UpdateStore;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static com.zerobase.storeapi.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final MemberClient memberClient;

    public StoreDto registerStore(Long sellerId, RegisterStore form) {
        checkDuplicateStoreName(form.getName());

        Store store = Store.of(sellerId, form);
        storeRepository.save(store);
        return StoreDto.from(store);
    }

    /**
     * 매장 이름 중복 확인
     * exception : DUPLICATE_STORE_NAME "매장명은 중복일 수 없습니다."
     *
     * @param name
     */
    private void checkDuplicateStoreName(String name) {
        boolean exists = storeRepository.existsByName(name);

        if (exists) {
            throw new StoreException(DUPLICATE_STORE_NAME);
        }
    }

    @Transactional
    public StoreDto updateStore(Long sellerId, UpdateStore form) {
        checkDuplicateStoreName(form.getName());
        Store store = checkMatchSellerAndStore(sellerId, form.getId());
        checkStoreAlreadyDeleted(store.getDeletedAt());
        store.update(form);
        return StoreDto.from(store);
    }

    private void checkStoreAlreadyDeleted(LocalDate deletedAt) {
        if(deletedAt != null) {
            throw new StoreException(ALREADY_DELETED_STORE);
        }
    }


    private Store checkMatchSellerAndStore(Long sellerId, Long id) {
        return storeRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new StoreException(UNMATCHED_SELLER_STORE));
    }

    @Transactional
    public DeleteStore deleteStore(Long sellerId, Long id) {
        Store store = checkMatchSellerAndStore(sellerId, id);
        store.delete();

        // 아이템 삭제 코드 추가 필요
        return DeleteStore.builder().deletedAt(store.getDeletedAt()).build();
    }
}
