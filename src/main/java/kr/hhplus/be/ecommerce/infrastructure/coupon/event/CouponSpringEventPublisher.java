package kr.hhplus.be.ecommerce.infrastructure.coupon.event;

import kr.hhplus.be.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.be.ecommerce.domain.coupon.CouponEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponSpringEventPublisher implements CouponEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void used(CouponEvent.Used event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void useFailed(CouponEvent.UseFailed event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void canceled(CouponEvent.Canceled event) {
        eventPublisher.publishEvent(event);
    }
}
