package kr.hhplus.be.ecommerce.support.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    String key();
    LockType type();
    long waitTime() default 5L;
    long leaseTime() default 3L;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    LockStrategy strategy() default LockStrategy.PUB_SUB_LOCK;
}
