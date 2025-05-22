package kr.hhplus.be.ecommerce.domain.balance;

public interface BalanceEventPublisher {

    void used(BalanceEvent.Used event);

    void useFailed(BalanceEvent.UseFailed event);
}
