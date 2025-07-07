package kr.hhplus.be.ecommerce.lock.domain;

import org.springframework.transaction.support.TransactionSynchronization;

import java.util.concurrent.TimeUnit;

import static org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive;
import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;

public abstract class DefaultLockTemplate implements LockTemplate {

    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) throws Throwable {
        try {
            acquireLock(key, waitTime, leaseTime, timeUnit);
            return callback.doInLock();
        } finally {
            if (isActualTransactionActive()) {
                registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        releaseLock(key);
                    }
                });
            } else {
                releaseLock(key);
            }
        }
    }

    public abstract void acquireLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    public abstract void releaseLock(String key);

}
