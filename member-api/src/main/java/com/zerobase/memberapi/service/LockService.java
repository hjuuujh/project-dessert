package com.zerobase.memberapi.service;


import com.zerobase.memberapi.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.zerobase.memberapi.exception.ErrorCode.ACCOUNT_TRANSACTION_LOCK;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient; // 이름이 같으면 Bean 자동 주입

    public void lock(String email) {
        // user의 이메일을 lock key로 사용
        RLock lock = redissonClient.getLock(getLockKey(email));
        log.info("Trying lock for email : {}", email);

        try {
            // 5초동안 안무거도안하면 lock 잃음
            // 1초 기다리는 동안 lock 풀리지않으면 취득 못함
            boolean isLock = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLock) {
                log.error("===Lock acquisition failed===");
                throw new MemberException(ACCOUNT_TRANSACTION_LOCK);
            }
        }catch (MemberException e){
            throw e;

        }catch (Exception e) {
            log.error("Redis lock failed", e);
        }


    }

    public void unlock(String email) {
        log.debug("Un lock for accountNumber : {}", email);
        redissonClient.getLock(getLockKey(email)).unlock();

    }

    private static String getLockKey(String email) {
        return "ACLK" + email;
    }
}