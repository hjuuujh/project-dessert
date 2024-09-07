package com.zerobase.storeapi.controller;

import com.zerobase.storeapi.service.StoreItemSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store/item/search")
public class StoreItemSearchController {
    private final StoreItemSearchService storeItemSearchService;

    @GetMapping("/order")
    public ResponseEntity<?> searchItemByKeyword(@RequestParam String keyword, final Pageable pageable) {
        return ResponseEntity.ok(storeItemSearchService.searchItemByKeyword(keyword, pageable));
    }

    @GetMapping("/category")
    public ResponseEntity<?> searchItemByCategory(@RequestParam String keyword,
                                                  @RequestParam String category, final Pageable pageable) {
        return ResponseEntity.ok(storeItemSearchService.searchItemByCategory(keyword, category, pageable));
    }

    @GetMapping("/new")
    public ResponseEntity<?> searchItemByNewest(@RequestParam String keyword, final Pageable pageable) {
        return ResponseEntity.ok(storeItemSearchService.searchItemByNewest(keyword, pageable));
    }

    @GetMapping("/price/low")
    public ResponseEntity<?> searchItemByLowerPrice(@RequestParam String keyword, final Pageable pageable) {
        return ResponseEntity.ok(storeItemSearchService.searchItemByLowerPrice(keyword, pageable));
    }

    @GetMapping("/price/high")
    public ResponseEntity<?> searchItemByHighPrice(@RequestParam String keyword, final Pageable pageable) {
        return ResponseEntity.ok(storeItemSearchService.searchItemByHighPrice(keyword, pageable));
    }

    @GetMapping("/store")
    public ResponseEntity<?> searchStoreItem(@RequestParam Long storeId, final Pageable pageable) {
        return ResponseEntity.ok(storeItemSearchService.searchStoreItem(storeId, pageable));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> searchItem(@RequestParam Long itemId) {
        return ResponseEntity.ok(storeItemSearchService.searchItem(itemId));
    }
}
