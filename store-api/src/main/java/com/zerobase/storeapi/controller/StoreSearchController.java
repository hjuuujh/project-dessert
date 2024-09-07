package com.zerobase.storeapi.controller;

import com.zerobase.storeapi.service.StoreSearchService;
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
@RequestMapping("/api/store/search")
public class StoreSearchController {
    private final StoreSearchService storeSearchService;

    @GetMapping
    public ResponseEntity<?> searchStoreByKeyword(@RequestParam String keyword, final Pageable pageable) {

        return ResponseEntity.ok(storeSearchService.searchStoreByKeyword(keyword, pageable));
    }

    @GetMapping("/follow")
    public ResponseEntity<?> searchStoreByFollowOrder(@RequestParam String keyword, final Pageable pageable) {

        return ResponseEntity.ok(storeSearchService.searchStoreByFollowOrder(keyword, pageable));
    }

    @GetMapping("/detail")
    public ResponseEntity<?> searchStore(@RequestParam Long id) {

        return ResponseEntity.ok(storeSearchService.searchStore(id));
    }
}
