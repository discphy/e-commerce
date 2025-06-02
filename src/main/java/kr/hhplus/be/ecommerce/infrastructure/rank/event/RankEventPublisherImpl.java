package kr.hhplus.be.ecommerce.infrastructure.rank.event;

import kr.hhplus.be.ecommerce.domain.rank.RankEvent;
import kr.hhplus.be.ecommerce.domain.rank.RankEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankEventPublisherImpl implements RankEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void created(RankEvent.Created event) {
        applicationEventPublisher.publishEvent(event);
    }
}
