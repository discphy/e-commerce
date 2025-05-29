package kr.hhplus.be.ecommerce.infrastructure.coupon.event;

import kr.hhplus.be.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.be.ecommerce.domain.coupon.CouponEventPublisher;
import kr.hhplus.be.ecommerce.support.event.EventType;
import kr.hhplus.be.ecommerce.support.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventPublisherImpl implements CouponEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    public void publishRequested(CouponEvent.PublishRequested event) {
        outboxEventPublisher.publishManualEvent(EventType.COUPON_PUBLISH_REQUESTED, event.getCouponId(), event);
    }

    @Override
    public void published(CouponEvent.Published event) {
        eventPublisher.publishEvent(event);
    }
}
