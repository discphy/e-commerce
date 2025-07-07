package kr.hhplus.be.ecommerce.product.infrastructure;

import kr.hhplus.be.ecommerce.product.domain.rank.RankEvent;
import kr.hhplus.be.ecommerce.product.domain.rank.RankEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankCoreEventPublisher implements RankEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void created(RankEvent.Created event) {
        applicationEventPublisher.publishEvent(event);
    }
}
