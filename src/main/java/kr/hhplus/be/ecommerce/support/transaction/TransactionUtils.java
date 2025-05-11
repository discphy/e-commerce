package kr.hhplus.be.ecommerce.support.transaction;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionUtils {

    public static void executeAfterTransaction(Runnable action) {
        if (hasNotActiveTransaction()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                action.run();
            }
        });
    }

    public static boolean hasNotActiveTransaction() {
        return !TransactionSynchronizationManager.isActualTransactionActive();
    }
}
