package kr.hhplus.be.ecommerce.lock.domain;

@FunctionalInterface
public interface LockCallback<T> {

    T doInLock() throws Throwable;
}
