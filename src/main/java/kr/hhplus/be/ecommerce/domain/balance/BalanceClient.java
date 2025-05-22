package kr.hhplus.be.ecommerce.domain.balance;

public interface BalanceClient {

    BalanceInfo.User getUser(Long userId);
}
