package kr.hhplus.be.ecommerce.balance.domain;

public interface BalanceClient {

    BalanceInfo.User getUser(Long userId);
}
