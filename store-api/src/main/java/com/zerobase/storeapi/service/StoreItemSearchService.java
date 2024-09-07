package com.zerobase.storeapi.service;

import com.zerobase.storeapi.domain.dto.ItemDto;
import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.form.item.SearchItem;
import com.zerobase.storeapi.domain.type.Category;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.zerobase.storeapi.exception.ErrorCode.NOT_FOUND_ITEM;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreItemSearchService {
    private final StoreItemRepository storeItemRepository;

    // 기본 : 키워드 + 판매순
    public Page<SearchItem> searchItemByKeyword(String keyword, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseOrderByOrderCountDesc(keyword, pageable)
                .map(SearchItem::from);
    }

    // 키워드 + 최신순
    public Page<SearchItem> searchItemByNewest(String keyword, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseOrderByModifiedAtDesc(keyword, pageable)
                .map(SearchItem::from);
    }

    // 키워드 + 낮은 가격순
    public Page<SearchItem> searchItemByLowerPrice(String keyword, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseOrderByPrice(keyword, pageable)
                .map(SearchItem::from);
    }

    // 키워드 + 높은 가격순
    public Page<SearchItem> searchItemByHighPrice(String keyword, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseOrderByPriceDesc(keyword, pageable)
                .map(SearchItem::from);
    }

    public Page<SearchItem> searchItemByCategory(String keyword, String category, Pageable pageable) {
        return storeItemRepository.findByNameContainingIgnoreCaseAndCategory(keyword, Category.valueOf(category.toUpperCase()), pageable)
                .map(SearchItem::from);
    }

    public Page<SearchItem> searchStoreItem(Long storeId, Pageable pageable) {
        return storeItemRepository.findByStoreId(storeId, pageable)
                .map(SearchItem::from);
    }

    public ItemDto searchItem(Long itemId) {
        Item item =  storeItemRepository.findById(itemId)
                .orElseThrow(()->new StoreException(NOT_FOUND_ITEM));
        return ItemDto.from(item);
    }
}
