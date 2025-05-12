package kr.hhplus.be.ecommerce.support.lock;

import java.util.concurrent.TimeUnit;

import static kr.hhplus.be.ecommerce.support.transaction.TransactionUtils.executeAfterTransaction;
import static kr.hhplus.be.ecommerce.support.transaction.TransactionUtils.hasNotActiveTransaction;

public abstract class DefaultLockTemplate implements LockTemplate {

    @Override
    public <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) throws Throwable {
        try {
            acquireLock(key, waitTime, leaseTime, timeUnit);
            executeAfterTransaction(() -> releaseLock(key));

            return callback.doInLock();
        } finally {
            if (hasNotActiveTransaction()) {
                releaseLock(key);
            }
        }
    }

    public abstract void acquireLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    public abstract void releaseLock(String key);

}
