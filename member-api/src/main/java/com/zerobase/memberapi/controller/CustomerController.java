package com.zerobase.memberapi.controller;

import com.zerobase.memberapi.aop.BalanceLock;
import com.zerobase.memberapi.client.from.FollowForm;
import com.zerobase.memberapi.client.from.HeartForm;
import com.zerobase.memberapi.domain.member.form.ChargeForm;
import com.zerobase.memberapi.security.TokenProvider;
import com.zerobase.memberapi.service.CustomerService;
import com.zerobase.memberapi.service.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/customer")
public class CustomerController {
    private final CustomerService customerService;
    private final TokenProvider tokenProvider;
    private final ValidationErrorResponse validationErrorResponse;

    /**
     *
     * @param token
     * @param form amount
     * @param errors
     * @return
     */
    @PostMapping("/charge")
    @BalanceLock
    public ResponseEntity<?> chargeBalance(@RequestHeader(name = "Authorization") String token,
                                           @RequestBody @Valid ChargeForm form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(customerService.chargeBalance(tokenProvider.getUserIdFromToken(token), form));
    }
    @GetMapping
    public ResponseEntity<?> getCustomer(@RequestHeader(name = "Authorization") String token){
        return ResponseEntity.ok(customerService.getCustomer(tokenProvider.getUserIdFromToken(token)));
    }

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestHeader(name = "Authorization") String token, @RequestBody FollowForm form) {

        return ResponseEntity.ok(customerService.follow(tokenProvider.getUserIdFromToken(token), form.getStoreId()));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollow(@RequestHeader(name = "Authorization") String token, @RequestBody FollowForm form) {

        return ResponseEntity.ok(customerService.unfollow(tokenProvider.getUserIdFromToken(token), form.getStoreId()));
    }

    @GetMapping("/stores")
    public ResponseEntity<?> getFollowStores(@RequestHeader(name = "Authorization") String token,
                                             Pageable pageable) {
        return ResponseEntity.ok(customerService.getFollowStores(tokenProvider.getUserIdFromToken(token), pageable));
    }

    @PostMapping("/heart")
    public ResponseEntity<?> heart(@RequestHeader(name = "Authorization") String token, @RequestBody HeartForm form) {

        return ResponseEntity.ok(customerService.heart(tokenProvider.getUserIdFromToken(token), form));
    }

    @PostMapping("/unheart")
    public ResponseEntity<?> unheart(@RequestHeader(name = "Authorization") String token, @RequestBody HeartForm form) {

        return ResponseEntity.ok(customerService.unheart(tokenProvider.getUserIdFromToken(token), form));
    }

    @GetMapping("/items")
    public ResponseEntity<?> getHeartItems(@RequestHeader(name = "Authorization") String token,
                                           Pageable pageable) {
        return ResponseEntity.ok(customerService.getHeartItems(tokenProvider.getUserIdFromToken(token), pageable));
    }

    @GetMapping("/delete/heart")
    public ResponseEntity<?> deleteHeartItem(@RequestParam("itemId") Long id) {
        customerService.deleteHeartItem(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete/follow")
    public ResponseEntity<?> deleteFollowStore(@RequestParam("storeId") Long storeId) {
        customerService.deleteFollowStore(storeId);
        return ResponseEntity.ok().build();

    }
}