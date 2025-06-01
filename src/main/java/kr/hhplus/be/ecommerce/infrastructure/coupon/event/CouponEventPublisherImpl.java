package kr.hhplus.be.ecommerce.infrastructure.coupon.event;

import kr.hhplus.be.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.be.ecommerce.domain.coupon.CouponEventPublisher;
import kr.hhplus.be.ecommerce.support.event.EventType;
import kr.hhplus.be.ecommerce.support.message.DefaultMessage;
import kr.hhplus.be.ecommerce.support.message.Message;
import kr.hhplus.be.ecommerce.support.message.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventPublisherImpl implements CouponEventPublisher {

    private final ApplicationEventPublisher eventPublisher;
    private final MessageProducer messageProducer;

    @Override
    public void publishRequested(CouponEvent.PublishRequested event) {
        Message message = DefaultMessage.of(EventType.COUPON_PUBLISH_REQUESTED, event.getCouponId(), event);
        messageProducer.send(message);
    }

    @Override
    public void published(CouponEvent.Published event) {
        eventPublisher.publishEvent(event);
    }
}
