package com.zerobase.storeapi.controller;

import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.domain.form.item.CreateItem;
import com.zerobase.storeapi.domain.form.item.UpdateItem;
import com.zerobase.storeapi.service.StoreItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store/item")
public class StoreItemController {
    private final MemberClient memberClient;
    private final StoreItemService storeItemService;

    @PostMapping
    public ResponseEntity<?> createItem(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody CreateItem form, Errors errors) {
        List<ErrorResponse> errorResponses = checkValidation(errors);
        if (!errorResponses.isEmpty()) {
            return new ResponseEntity<>(errorResponses, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(storeItemService.createItem(memberClient.getMemberId(token), form));
    }

    @PutMapping
    public ResponseEntity<?> updateItem(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody UpdateItem form, Errors errors) {
        List<ErrorResponse> errorResponses = checkValidation(errors);
        if (!errorResponses.isEmpty()) {
            return new ResponseEntity<>(errorResponses, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(storeItemService.updateItem(memberClient.getMemberId(token), form));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteItem(@RequestHeader(name = "Authorization") String token,
                                        @RequestParam Long id) {

        return ResponseEntity.ok(storeItemService.deleteItem(memberClient.getMemberId(token), id));
    }

    /**
     * validation 에러 메세지 리스트를 리턴하는 클래스
     *
     * @param errors
     * @return
     */
    private List<ErrorResponse> checkValidation(Errors errors) {
        List<ErrorResponse> errorResponses = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach(error -> {
                errorResponses.add(ErrorResponse.of((FieldError) error));
            });
        }

        return errorResponses;
    }

}
