package kr.hhplus.be.ecommerce.balance.infrastructure;

import kr.hhplus.be.ecommerce.balance.domain.BalanceClient;
import kr.hhplus.be.ecommerce.balance.domain.BalanceInfo;
import kr.hhplus.be.ecommerce.client.api.user.UserApiClient;
import kr.hhplus.be.ecommerce.client.api.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceCoreClient implements BalanceClient {

    private final UserApiClient userApiClient;

    @Override
    public BalanceInfo.User getUser(Long userId) {
        UserResponse.User user = userApiClient.getUser(userId).getData();
        return BalanceInfo.User.of(user.userId(), user.username());
    }
}
