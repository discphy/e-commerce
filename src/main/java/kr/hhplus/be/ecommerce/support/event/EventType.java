package kr.hhplus.be.ecommerce.support.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

    COUPON_PUBLISH_REQUESTED(Topic.COUPON_PUBLISH_REQUESTED),
    COUPON_PUBLISHED(Topic.COUPON_PUBLISHED),

    ORDER_CREATED(Topic.ORDER_CREATED),
    ORDER_COMPLETED(Topic.ORDER_COMPLETED),
    ORDER_COMPLETE_FAILED(Topic.ORDER_COMPLETE_FAILED),

    PAYMENT_PAID(Topic.PAYMENT_PAID),
    PAYMENT_FAILED(Topic.PAYMENT_FAILED),
    PAYMENT_CANCELED(Topic.PAYMENT_CANCELED),
    ;

    private final String topic;

    public static class Topic {

        // Outside 이벤트 토픽
        public static final String COUPON_PUBLISH_REQUESTED = "outside.coupon.publish.requested.v1";
        public static final String ORDER_COMPLETE_FAILED = "outside.order.complete.failed.v1";
        public static final String ORDER_COMPLETED = "outside.order.completed.v1";
        public static final String ORDER_CREATED = "outside.order.created.v1";
        public static final String PAYMENT_PAID = "outside.payment.paid.v1";
        public static final String PAYMENT_FAILED = "outside.payment.failed.v1";
        public static final String PAYMENT_CANCELED = "outside.payment.canceled.v1";

        // Inside 이벤트 토픽
        public static final String COUPON_PUBLISHED = "inside.coupon.published.v1";
    }

    public static class GroupId {

        // Outside 이벤트 컨슈머 그룹 ID
        public static final String OUTBOX = "outbox-event-listener-group";
        public static final String COUPON = "coupon-event-listener-group";
        public static final String ORDER = "order-event-listener-group";
        public static final String PAYMENT = "payment-event-listener-group";
    }
}
