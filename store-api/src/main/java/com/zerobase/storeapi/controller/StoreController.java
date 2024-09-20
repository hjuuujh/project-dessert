package com.zerobase.storeapi.controller;

import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.client.from.FollowForm;
import com.zerobase.storeapi.client.from.MatchForm;
import com.zerobase.storeapi.client.from.StoresForm;
import com.zerobase.storeapi.domain.form.store.RegisterStore;
import com.zerobase.storeapi.domain.form.store.UpdateStore;
import com.zerobase.storeapi.service.StoreService;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {
    private final StoreService storeService;
    private final MemberClient memberClient;

    @PostMapping
    public ResponseEntity<?> registerStore(@RequestHeader(name = "Authorization") String token,
                                           @RequestBody @Validated RegisterStore form, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }

        return ResponseEntity.ok(storeService.registerStore(memberClient.getMemberId(token), form));
    }

    @PutMapping
    public ResponseEntity<?> updateStore(@RequestHeader(name = "Authorization") String token,
                                         @RequestBody @Validated UpdateStore form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }

        return ResponseEntity.ok(storeService.updateStore(memberClient.getMemberId(token), form));
    }

    @PatchMapping
    public ResponseEntity<?> deleteStore(@RequestHeader(name = "Authorization") String token,
                                         @RequestParam Long id) {

        return ResponseEntity.ok(storeService.deleteStore(memberClient.getMemberId(token), id));
    }


    @PostMapping("/follow")
    public ResponseEntity<?> increaseFollow(@RequestBody @Validated FollowForm form , BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }

        return ResponseEntity.ok(storeService.increaseFollow(form));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> decreaseFollow(@RequestBody @Validated FollowForm form) {

        return ResponseEntity.ok(storeService.decreaseFollow(form));
    }

    @PostMapping("/list")
    public ResponseEntity<?> getStores(@RequestBody StoresForm form
            , Pageable pageable) {
        return ResponseEntity.ok(storeService.getStores(form, pageable));
    }

    @GetMapping("/match")
    public boolean isMatchedStoreAndSeller(@RequestBody MatchForm form){
        return storeService.isMatchedStoreAndSeller(form);

    }

}
