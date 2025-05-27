package kr.hhplus.be.ecommerce.interfaces.coupon.event;

import kr.hhplus.be.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.be.ecommerce.domain.coupon.CouponEventPublisher;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderProcessTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponOrderEventListener {

    private final CouponService couponService;
    private final CouponEventPublisher couponEventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderEvent.Created event) {
        log.info("주문 생성 이벤트 수신 - 쿠폰 사용");
        try {
            Optional.ofNullable(event.getUserCouponId())
                .ifPresent(couponService::useUserCoupon);

            couponEventPublisher.used(createUsedEvent(event));
        } catch (Exception e) {
            log.error("주문 생성 이벤트 수신 - 쿠폰 사용 에러", e);
            couponEventPublisher.useFailed(createUseFailedEvent(event));
        }
    }

    @Async
    @EventListener
    public void handle(OrderEvent.Failed event) {
        log.info("주문 실패 이벤트 수신 - 쿠폰 사용 취소");
        if (event.getProcesses().isSuccess(OrderProcessTask.COUPON_USED)) {
            log.info("주문 실패 이벤트 수신 - 쿠폰 사용 취소 수행");
            Optional.ofNullable(event.getUserCouponId()).ifPresent(couponService::cancelUserCoupon);
        }
    }

    private CouponEvent.Used createUsedEvent(OrderEvent.Created event) {
        return CouponEvent.Used.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .orderProducts(event.getOrderProducts().stream()
                .map(op -> CouponEvent.OrderProduct.builder()
                    .orderProductId(op.getOrderProductId())
                    .productId(op.getProductId())
                    .productName(op.getProductName())
                    .unitPrice(op.getUnitPrice())
                    .quantity(op.getQuantity())
                    .build()
                ).toList()
            ).build();
    }

    private CouponEvent.UseFailed createUseFailedEvent(OrderEvent.Created event) {
        return CouponEvent.UseFailed.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .userCouponId(event.getUserCouponId())
            .totalPrice(event.getTotalPrice())
            .discountPrice(event.getDiscountPrice())
            .build();
    }
}
