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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final CustomerService customerService;
    private final SellerService sellerService;
    private final TokenProvider tokenProvider;
    private final ValidationErrorResponse validationErrorResponse;

    /**
     * 고객 정보 등록
     *
     * @param form   : email, name, password, phone, roles
     * @param errors : form의 validation 체크후 잘못된 형식의 메세지 리턴
     * @return 저장된 유저 정보
     */
    @PostMapping("/signup/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody @Valid SignUp form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> registerSeller(@RequestBody @Valid SignUp form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> signInCustomer(@RequestBody @Valid SignIn form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> signInSeller(@RequestBody @Valid SignIn form, Errors errors) {
        List<ResponseError> responseErrors = validationErrorResponse.checkValidation(errors);
        if (!responseErrors.isEmpty()) {
            return new ResponseEntity<>(responseErrors, HttpStatus.BAD_REQUEST);
        }

        SellerDto sellerDto = sellerService.signInMember(form);
        String token = tokenProvider.generateToken(sellerDto.getId(), sellerDto.getEmail(), sellerDto.getRoles());

        return ResponseEntity.ok(TokenResponse.from(token));
    }



}