package kr.hhplus.be.ecommerce.coupon.domain;


public interface CouponEventPublisher {

    void publishRequested(CouponEvent.PublishRequested event);

    void published(CouponEvent.Published event);
}
