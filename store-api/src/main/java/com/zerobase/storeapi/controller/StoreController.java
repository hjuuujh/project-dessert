package com.zerobase.storeapi.controller;

import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.client.from.FollowForm;
import com.zerobase.storeapi.client.from.StoresForm;
import com.zerobase.storeapi.domain.form.store.RegisterStore;
import com.zerobase.storeapi.domain.form.store.UpdateStore;
import com.zerobase.storeapi.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {
    private final StoreService storeService;
    private final MemberClient memberClient;

    @PostMapping
    public ResponseEntity<?> registerStore(@RequestHeader(name = "Authorization") String token,
                                           @RequestBody @Valid RegisterStore form, Errors errors) {

        List<ErrorResponse> errorResponses = checkValidation(errors);
        if (!errorResponses.isEmpty()) {
            return new ResponseEntity<>(errorResponses, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(storeService.registerStore(memberClient.getMemberId(token), form));
    }

    @PutMapping
    public ResponseEntity<?> updateStore(@RequestHeader(name = "Authorization") String token,
                                         @RequestBody @Valid UpdateStore form, Errors errors) {

        List<ErrorResponse> errorResponses = checkValidation(errors);
        if (!errorResponses.isEmpty()) {
            return new ResponseEntity<>(errorResponses, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(storeService.updateStore(memberClient.getMemberId(token), form));
    }

    @PatchMapping
    public ResponseEntity<?> deleteStore(@RequestHeader(name = "Authorization") String token,
                                         @RequestParam Long id) {

        return ResponseEntity.ok(storeService.deleteStore(memberClient.getMemberId(token), id));
    }


    @PostMapping("/follow")
    public ResponseEntity<?> increaseFollow(@RequestBody FollowForm form) {

        return ResponseEntity.ok(storeService.increaseFollow(form));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> decreaseFollow(@RequestBody FollowForm form) {

        return ResponseEntity.ok(storeService.decreaseFollow(form));
    }

    @PostMapping("/list")
    public ResponseEntity<?> getStores(@RequestBody StoresForm form
            , Pageable pageable) {
        return ResponseEntity.ok(storeService.getStores(form, pageable));
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
