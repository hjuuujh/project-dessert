package com.zerobase.storeapi.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.zerobase.storeapi.domain.entity.QItem;
import com.zerobase.storeapi.domain.type.Category;
import com.zerobase.storeapi.domain.type.OrderBy;

public class ItemQueryHelper {

    // 필터링 수행
    public static BooleanBuilder createFilterBuilder(Category category, String keyword, QItem item) {
        BooleanBuilder filterBuilder = new BooleanBuilder();

        // 카테고리 필터링
        addCategoryFilter(category, item, filterBuilder);

        // 키워드 검색
        addKeywordFilter(keyword, item, filterBuilder);

        return filterBuilder;
    }
    // 정렬
    public static OrderSpecifier<?> getOrderSpecifier(OrderBy order, QItem item) {
        if (order == null) {
            // order 가 null이면 최신순으로 정렬
            return item.createdAt.desc();
        }

        switch (order) {
            case LATEST:
                return item.createdAt.desc();
            case POPULAR:
                return item.orderCount.desc();
            case HEART:
                return item.heartCount.desc();
            case LOW_PRICE:
                return item.price.asc();
            case HIGH_PRICE:
                return item.price.desc();
            default:
                return item.createdAt.desc();
        }
    }

    // 카테고리
    private static void addCategoryFilter(Category category, QItem item, BooleanBuilder filterBuilder) {
        if (category != null) {
            filterBuilder.and(item.category.eq(category));
        }
    }

    // 키워드 검색
    private static void addKeywordFilter(String keyword, QItem item, BooleanBuilder filterBuilder) {
        if (keyword != null) {
            filterBuilder.and(
                    item.name.containsIgnoreCase(keyword)
                            .or(item.description.containsIgnoreCase(keyword))
            );
        }
    }
}
