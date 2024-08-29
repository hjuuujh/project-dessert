package memberapi.service;


import com.zerobase.memberapi.exception.MemberException;
import com.zerobase.memberapi.service.LockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import static com.zerobase.memberapi.exception.ErrorCode.ACCOUNT_TRANSACTION_LOCK;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LockServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @InjectMocks
    private LockService lockService;

    @Test
    void successGetLock() throws InterruptedException {
        //given
        given(redissonClient.getLock(anyString()))
                .willReturn(lock);
        given(lock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(true);

        //when

        //then
        assertDoesNotThrow(() -> lockService.lock("123"));
    }

    @Test
    void failGetLock() throws InterruptedException {
        //given
        given(redissonClient.getLock(anyString()))
                .willReturn(lock);
        given(lock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(false);

        //when
        MemberException exception = assertThrows(MemberException.class, () -> lockService.lock("123"));

        //then
        assertEquals(ACCOUNT_TRANSACTION_LOCK, exception.getErrorCode());
    }
}