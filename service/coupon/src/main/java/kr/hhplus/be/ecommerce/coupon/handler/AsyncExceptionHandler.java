package kr.hhplus.be.ecommerce.coupon.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("비동기 예외 발생: {} - 메소드: {}, 파라미터: {}", ex.getMessage(), method.getName(), params, ex);
    }
}
