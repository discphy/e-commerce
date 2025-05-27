package kr.hhplus.be.ecommerce.interfaces.coupon.event;

import kr.hhplus.be.ecommerce.domain.coupon.CouponEvent;
import kr.hhplus.be.ecommerce.domain.coupon.CouponEventPublisher;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import kr.hhplus.be.ecommerce.domain.order.OrderProcesses;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class CouponOrderEventListenerUnitTest extends MockTestSupport {

    @InjectMocks
    private CouponOrderEventListener orderEventListener;

    @Mock
    private CouponService couponService;

    @Mock
    private CouponEventPublisher eventPublisher;

    @DisplayName("주문 생성 시, 쿠폰 사용 완료 이벤트를 발행한다.")
    @Test
    void handleCreated() {
        // given
        OrderEvent.Created event = mock(OrderEvent.Created.class);

        when(event.getUserCouponId()).
            thenReturn(1L);

        // when
        orderEventListener.handle(event);

        // then
        verify(couponService, times(1)).useUserCoupon(anyLong());
        verify(eventPublisher, times(1)).used(any(CouponEvent.Used.class));
    }

    @DisplayName("주문 생성 시, 쿠폰 사용 실패 이벤트를 발행한다.")
    @Test
    void handleCreatedWithFailed() {
        // given
        OrderEvent.Created event = mock(OrderEvent.Created.class);

        when(event.getUserCouponId()).
            thenReturn(1L);

        doThrow(new RuntimeException("쿠폰 사용 실패"))
            .when(couponService).useUserCoupon(anyLong());

        // when
        orderEventListener.handle(event);

        // then
        verify(eventPublisher, times(1)).useFailed(any(CouponEvent.UseFailed.class));
    }

    @DisplayName("주문 실패 시, 쿠폰 사용 취소한다.")
    @Test
    void handleFailed() {
        // given
        OrderEvent.Failed event = mock(OrderEvent.Failed.class);
        OrderProcesses processes = mock(OrderProcesses.class);

        when(event.getProcesses()).
            thenReturn(processes);

        when(event.getProcesses().isSuccess(any())).
            thenReturn(true);

        // when
        orderEventListener.handle(event);

        // then
        verify(couponService, times(1)).cancelUserCoupon(anyLong());
    }
}