package com.zerobase.memberapi.service;


import com.zerobase.memberapi.domain.member.form.ChargeForm;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.zerobase.memberapi.domain.member.dto.CustomerDto;
import com.zerobase.memberapi.domain.member.entity.Customer;
import com.zerobase.memberapi.domain.member.form.SignIn;
import com.zerobase.memberapi.domain.member.form.SignUp;
import com.zerobase.memberapi.exception.MemberException;
import com.zerobase.memberapi.repository.CustomerRepository;
import com.zerobase.memberapi.repository.SellerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zerobase.memberapi.exception.ErrorCode.*;


@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class CustomerService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;

    /**
     * Spring Security를 이용
     * 유저의 정보를 불러오기 위해서 구현
     *
     * @param email: email을 계정의 고유한 값으로 이용
     * @return 유저 정보
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    /**
     * form으로 받아온 고객 등록
     *
     * @param form : email, name, password, phone, roles
     * @return 저장된 사용자 정보
     */
    @Transactional
    public CustomerDto registerCustomer(SignUp form) {
        checkAlreadyExists(form);

        Customer customer = Customer.of(form, passwordEncoder.encode(form.getPassword()));

        Customer save = customerRepository.save(customer);
        return CustomerDto.from(save);
    }

    /**
     * 가입하려는 이메일이 이미 존재하는 이메일인지 확인
     * excpetion : ALREADY_REGISTERED_USER "이미 가입된 이메일입니다."
     *
     * @param form
     */
    private void checkAlreadyExists(SignUp form) {
        // 이미 등록된 이메일인 경우 예외 발생 : ALREADY_REGISTERED_USER "이미 가입된 이메일입니다."
        if (customerRepository.existsByEmail(form.getEmail()) || sellerRepository.existsByEmail(form.getEmail())) {
            throw new MemberException(ALREADY_REGISTERED_USER);
        }
    }

    /**
     * 이메일과 패스워드로 로그인
     *
     * @param form : email, password
     *             excpetion : LOGIN_CHECK_FAIL "이메일과 패스워드를 확인해주세요."
     * @return 이메일, 비밀번호 확인 통해 얻은 유저 정보
     */
    public CustomerDto signInMember(SignIn form) {
        // 이메일 이용해 유저정보 찾음
        // 이메일로 가입된 정보가 없는 경우 예외발생
        Customer member = customerRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new MemberException(NOT_FOUND_USER));

        // 로그인 시도한 비밀번호와 저장된 비밀번호가 같은지 확인
        if (!passwordEncoder.matches(form.getPassword(), member.getPassword())) {
            throw new MemberException(LOGIN_CHECK_FAIL);
        }

        return CustomerDto.from(member);
    }

    @Transactional
    public CustomerDto chargeBalance(Long id, ChargeForm form) {
        if (form.getAmount() < 0) {
            throw new MemberException(CHECK_AMOUNT);
        }
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new MemberException(NOT_FOUND_USER));

        customer.changeBalance(form.getAmount());

        return CustomerDto.from(customer);
    }

}