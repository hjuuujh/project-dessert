package com.zerobase.storeapi.service;

import com.zerobase.storeapi.domain.dto.StoreDto;
import com.zerobase.storeapi.domain.entity.Store;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.zerobase.storeapi.exception.ErrorCode.NOT_FOUND_STORE;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreSearchService {
    private final StoreRepository storeRepository;


    public Page<StoreDto> searchStoreByKeyword(String keyword, Pageable pageable) {
        return storeRepository.findByNameContainingIgnoreCaseAndDeletedAt(keyword, null, pageable)
                .map(StoreDto::from);
    }

    public Page<StoreDto> searchStoreByFollowOrder(String keyword, Pageable pageable) {
        return storeRepository.findByNameContainingIgnoreCaseAndDeletedAtOrderByFollowCountDesc(keyword, null, pageable)
                .map(StoreDto::from);
    }

    public StoreDto searchStore(Long id) {
        Store store = storeRepository.findById(id).orElseThrow(() -> new StoreException(NOT_FOUND_STORE));
        return StoreDto.from(store);
    }
}
