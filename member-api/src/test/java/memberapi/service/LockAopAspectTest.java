package memberapi.service;

import com.zerobase.memberapi.security.TokenProvider;
import com.zerobase.memberapi.service.LockAopAspect;
import com.zerobase.memberapi.service.LockService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {
    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private LockAopAspect lockAopAspect;

    @Test
    void lockAndUnlock() throws Throwable {
        //given
        ArgumentCaptor<String> lockArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockArg = ArgumentCaptor.forClass(String.class);

        String token = "Bearer token";
        given(tokenProvider.getUsernameFromToken(anyString()))
                .willReturn("aaa@gmail.com");

        //when
        lockAopAspect.aroundMethod(proceedingJoinPoint, token);

        //then
        verify(lockService, times(1)).lock(lockArg.capture());
        verify(lockService, times(1)).unlock(unlockArg.capture());
        assertEquals("aaa@gmail.com", lockArg.getValue());
        assertEquals("aaa@gmail.com", unlockArg.getValue());
    }

}