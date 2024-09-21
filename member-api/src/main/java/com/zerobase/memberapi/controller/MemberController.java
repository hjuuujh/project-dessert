package com.zerobase.memberapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.zerobase.memberapi.domain.member.dto.CustomerDto;
import com.zerobase.memberapi.domain.member.dto.SellerDto;
import com.zerobase.memberapi.domain.member.form.SignIn;
import com.zerobase.memberapi.domain.member.form.SignUp;
import com.zerobase.memberapi.domain.member.form.TokenResponse;
import com.zerobase.memberapi.security.TokenProvider;
import com.zerobase.memberapi.service.CustomerService;
import com.zerobase.memberapi.service.SellerService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final CustomerService customerService;
    private final SellerService sellerService;
    private final TokenProvider tokenProvider;

    /**
     * 고객 정보 등록
     *
     * @param form   : email, name, password, phone, roles
     * @param bindingResult : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return 저장된 유저 정보
     */
    @PostMapping("/signup/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody @Validated SignUp form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
         }

        return ResponseEntity.ok(customerService.registerCustomer(form));
    }

    /**
     * 셀러 정보 등록
     *
     * @param form   : email, name, password, phone, roles
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return 저장된 유저 정보
     */
    @PostMapping("/signup/seller")
    public ResponseEntity<?> registerSeller(@RequestBody @Validated SignUp form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }

        return ResponseEntity.ok(sellerService.registerSeller(form));
    }

    /**
     * 고객 이메일과 패스워드로 로그인
     *
     * @param form   : email, password
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return 토큰
     */
    @PostMapping("/signin/customer")
    public ResponseEntity<?> signInCustomer(@RequestBody @Validated SignIn form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }

        CustomerDto customerDto = customerService.signInMember(form);
        String token = tokenProvider.generateToken(customerDto.getId(), customerDto.getEmail(), customerDto.getRoles());

        return ResponseEntity.ok(TokenResponse.from(token));
    }

    /**
     * 고객 이메일과 패스워드로 로그인
     *
     * @param form   : email, password
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return 토큰
     */
    @PostMapping("/signin/seller")
    public ResponseEntity<?> signInSeller(@RequestBody @Validated SignIn form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ErrorResponse("400", "Validation failure", errors));
        }

        SellerDto sellerDto = sellerService.signInMember(form);
        String token = tokenProvider.generateToken(sellerDto.getId(), sellerDto.getEmail(), sellerDto.getRoles());

        return ResponseEntity.ok(TokenResponse.from(token));
    }

    /**
     * 사용자 정보 찾기
     *
     * @param token
     * @return 토큰으로 찾은 유저 아이디
     */
    @GetMapping("/id")
    public ResponseEntity<?> getMemberId(@RequestHeader(name = "Authorization") String token) {
        Long id = tokenProvider.getUserIdFromToken(token);
        return ResponseEntity.ok(id);
    }


}