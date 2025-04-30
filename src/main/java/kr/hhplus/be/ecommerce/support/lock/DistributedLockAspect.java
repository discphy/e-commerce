package kr.hhplus.be.ecommerce.support.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DistributedLockAspect {

    private final LockKeyGenerator generator;
    private final LockStrategyRegistry registry;

    @Around("@annotation(kr.hhplus.be.ecommerce.support.lock.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock lock = signature.getMethod().getAnnotation(DistributedLock.class);

        String key = generator.generateKey(signature.getParameterNames(), joinPoint.getArgs(), lock.key(), lock.type());
        LockTemplate template = registry.getLockTemplate(lock.strategy());

        return template.executeWithLock(key, lock.waitTime(), lock.leaseTime(), lock.timeUnit(), joinPoint::proceed);
    }
}
