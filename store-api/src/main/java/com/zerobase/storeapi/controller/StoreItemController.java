package com.zerobase.storeapi.controller;

import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.client.from.HeartForm;
import com.zerobase.storeapi.client.from.ItemsForm;
import com.zerobase.storeapi.domain.form.item.CreateItem;
import com.zerobase.storeapi.domain.form.item.UpdateItem;
import com.zerobase.storeapi.service.StoreItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store/item")
public class StoreItemController {
    private final MemberClient memberClient;
    private final StoreItemService storeItemService;

    @PostMapping
    public ResponseEntity<?> createItem(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody @Validated CreateItem form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }
        return ResponseEntity.ok(storeItemService.createItem(memberClient.getMemberId(token), form));
    }

    @PutMapping
    public ResponseEntity<?> updateItem(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody @Validated UpdateItem form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }
        return ResponseEntity.ok(storeItemService.updateItem(memberClient.getMemberId(token), form));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteItem(@RequestHeader(name = "Authorization") String token,
                                        @RequestParam Long id) {

        return ResponseEntity.ok(storeItemService.deleteItem(memberClient.getMemberId(token), id));
    }


    @PostMapping("/heart")
    public ResponseEntity<?> increaseHeart(@RequestBody @Validated HeartForm form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }
        return ResponseEntity.ok(storeItemService.increaseHeart(form));
    }

    @PostMapping("/unheart")
    public ResponseEntity<?> decreaseHeart(@RequestBody @Validated HeartForm form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }
        return ResponseEntity.ok(storeItemService.decreaseHeart(form));
    }

    @PostMapping("/list")
    public ResponseEntity<?> getItems(@RequestBody ItemsForm form, Pageable pageable) {
        return ResponseEntity.ok(storeItemService.getItems(form, pageable));
    }


}
