package kr.hhplus.be.ecommerce.support.lock;

@FunctionalInterface
public interface LockCallback<T> {

    T doInLock() throws Throwable;
}
