package com.zerobase.memberapi.security;

import com.zerobase.memberapi.service.CustomerService;
import com.zerobase.memberapi.service.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // Spring Security 설정
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SellerService sellerService;
    private final CustomerService customerService;

    @Order(1)
    @Configuration
    @EnableWebSecurity
    class CustomerSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.httpBasic().disable() // rest api 이므로 기본설정 사용안함
                    .csrf().disable() // rest api이므로 csrf 보안이 필요없어 disable처리
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token으로 인증하므로 세션은 필요없으므로 생성안함
                    .and()
                    .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
                    .antMatchers("/api/member/signup/**", "/api/member/signin/**").permitAll() // 가입 및 인증 주소는 누구나 접근가능
                    .and()
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        }
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(customerService);
        }
    }

    @Order(2)
    @Configuration
    @EnableWebSecurity
    class SellerSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.httpBasic().disable() // rest api 이므로 기본설정 사용안함
                    .csrf().disable() // rest api이므로 csrf 보안이 필요없어 disable처리
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token으로 인증하므로 세션은 필요없으므로 생성안함
                    .and()
                    .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
                    .antMatchers("/api/member/signup/**", "/api/member/signin/**").permitAll() // 가입 및 인증 주소는 누구나 접근가능
                    .and()
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        }
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(sellerService);
        }
    }

}