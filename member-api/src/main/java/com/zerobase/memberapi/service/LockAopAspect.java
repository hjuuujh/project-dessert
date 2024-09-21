package com.zerobase.memberapi.service;

import com.zerobase.memberapi.aop.BalanceLockIdInterface;
import com.zerobase.memberapi.domain.member.entity.Customer;
import com.zerobase.memberapi.exception.ErrorCode;
import com.zerobase.memberapi.exception.MemberException;
import com.zerobase.memberapi.repository.CustomerRepository;
import com.zerobase.memberapi.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

    private final LockService lockService;
    private final TokenProvider tokenProvider;
    private final CustomerRepository customerRepository;

    /**
     * @Before : 메소드가 실행되기 이전에 실행
     * @After : 메소드의 종료 후 무조건 실행 try-catch 의 finally 같이
     * @After-returning : 메소드가 성공적으로 완료되고 리턴한 다음 실행
     * @After-throwing : 메소드 실행중 예외가 발생하면 실행 try-catch 의 catch 같이
     * @Around : 메소드 호출 자체를 가로채서 메소드 실행 전후에 처리할 로직 삽입
     */
    // args 는 annotaion 붙은 함수의 파리미터와 순서, 개수 맞아야 aop 실행함
    // toekn, .. : 맨 처음이 token이고 뒤에 파라미터가 있는경우
    @Around(value = "@annotation(com.zerobase.memberapi.aop.BalanceLock) && args(token, form, ..)")
    public Object customerAroundMethod(ProceedingJoinPoint joinPoint,
                               String token,
                               BalanceLockIdInterface form) throws Throwable{
        
        String withoutBearer = tokenWithoutBearer(token); // Bearer 제외

        // 얘가 chrgebalance면 토큰으로 이메일 구해서 바로 락 취득
        if("chargeBalance".equals(joinPoint.getSignature().getName())) {
            lockService.lock(tokenProvider.getUsernameFromToken(withoutBearer));
        }else {
            // 그외 넘어온 customerid로 이메일 구해서 락 취득
            Customer customer = customerRepository.findById(form.getCustomerId()).orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_USER));
            lockService.lock(customer.getEmail());
        }


        // lock 취득 시도
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            return e.fillInStackTrace();
        }
        finally {
            // lock 해제
            lockService.unlock(tokenProvider.getUsernameFromToken(withoutBearer));
        }

    }

    @Around(value = "@annotation(com.zerobase.memberapi.aop.IncomeLock) && args(token, ..)")
    public Object sellerAroundMethod(ProceedingJoinPoint joinPoint,
                                     String token) throws Throwable{

        // lock 취득 시도
        String withoutBearer = tokenWithoutBearer(token); // Bearer 제외
        lockService.lock(tokenProvider.getUsernameFromToken(withoutBearer));

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            return e.fillInStackTrace();
        }finally {
            // lock 해제
            lockService.unlock(tokenProvider.getUsernameFromToken(withoutBearer));
        }
    }

    private String tokenWithoutBearer(String token){
        return token.substring(7);
    }
}
