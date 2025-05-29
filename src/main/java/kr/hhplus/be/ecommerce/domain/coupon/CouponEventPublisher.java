package kr.hhplus.be.ecommerce.domain.coupon;

public interface CouponEventPublisher {

    void publishRequested(CouponEvent.PublishRequested event);

    void published(CouponEvent.Published event);
}
