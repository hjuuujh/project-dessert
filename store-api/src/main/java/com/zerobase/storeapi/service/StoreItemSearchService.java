package com.zerobase.storeapi.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.storeapi.domain.dto.ItemDto;
import com.zerobase.storeapi.domain.dto.ItemSearchDto;
import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.entity.QItem;
import com.zerobase.storeapi.domain.form.item.SearchItem;
import com.zerobase.storeapi.domain.type.Category;
import com.zerobase.storeapi.domain.type.OrderBy;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.ItemQueryHelper;
import com.zerobase.storeapi.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.storeapi.domain.entity.QItem.item;
import static com.zerobase.storeapi.exception.ErrorCode.NOT_FOUND_ITEM;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreItemSearchService {
    private final StoreItemRepository storeItemRepository;
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

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

    public ItemDto detailItem(Long itemId) {
        Item item = storeItemRepository.findById(itemId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));
        return ItemDto.from(item);
    }

    public Page<ItemSearchDto> searchItem(String keyword, Category category, OrderBy order, Pageable pageable) {
        // 필터링
        BooleanBuilder filterBuilder = ItemQueryHelper.createFilterBuilder(category, keyword, item);

        // 정렬
        OrderSpecifier<?> orderSpecifier = ItemQueryHelper.getOrderSpecifier(order, item);

        // 필터링 및 정렬 적용
        List<Item> result = getFilterAndSortResult(orderSpecifier,filterBuilder,pageable);

        // 전체 카운트 조회 쿼리
        long totalCount = getCount(filterBuilder);

        // dto로 변환
        List<ItemSearchDto> itemList = result.stream().map(ItemSearchDto::from).collect(Collectors.toList());

        return new PageImpl<>(itemList, pageable, totalCount);
    }

    // 필터링 및 정렬 수행
    private List<Item> getFilterAndSortResult(OrderSpecifier orderSpecifier, BooleanBuilder filterBuilder, Pageable pageable) {
        return queryFactory.selectFrom(item)
                .where(filterBuilder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    // count 조회
    private Long getCount(BooleanBuilder filterBuilder) {
        return queryFactory.select(item.count())
                .from(item)
                .where(filterBuilder)
                .fetchOne();
    }
}
