package kr.hhplus.be.ecommerce.infrastructure.balance.client;

import kr.hhplus.be.ecommerce.domain.balance.BalanceClient;
import kr.hhplus.be.ecommerce.domain.balance.BalanceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceApiClient implements BalanceClient {

    @Override
    public BalanceInfo.User getUser(Long userId) {
        return null;
    }
}
