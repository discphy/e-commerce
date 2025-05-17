package kr.hhplus.be.ecommerce.application.order;

import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.domain.coupon.CouponInfo;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.domain.payment.PaymentService;
import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import kr.hhplus.be.ecommerce.domain.stock.StockService;
import kr.hhplus.be.ecommerce.domain.user.UserCouponInfo;
import kr.hhplus.be.ecommerce.domain.user.UserCouponService;
import kr.hhplus.be.ecommerce.domain.user.UserService;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class OrderFacadeUnitTest extends MockTestSupport {

    @InjectMocks
    private OrderFacade orderFacade;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private UserCouponService userCouponService;

    @Mock
    private CouponService couponService;

    @Mock
    private OrderService orderService;

    @Mock
    private BalanceService balanceService;

    @Mock
    private StockService stockService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RankService rankService;

    @DisplayName("주문 결제를 한다.")
    @Test
    void orderPayment() {
        // given
        OrderCriteria.OrderPayment criteria = mock(OrderCriteria.OrderPayment.class);
        ProductInfo.OrderProducts mockOrderProducts = mock(ProductInfo.OrderProducts.class);
        UserCouponInfo.UsableCoupon mockUsableCoupon = mock(UserCouponInfo.UsableCoupon.class);
        CouponInfo.Coupon mockCoupon = mock(CouponInfo.Coupon.class);
        OrderInfo.Order mockOrder = mock(OrderInfo.Order.class);

        when(productService.getOrderProducts(any())).thenReturn(mockOrderProducts);
        when(userCouponService.getUsableCoupon(any())).thenReturn(mockUsableCoupon);
        when(couponService.getCoupon(any())).thenReturn(mockCoupon);
        when(orderService.createOrder(any())).thenReturn(mockOrder);

        // when
        orderFacade.orderPayment(criteria);

        // then
        InOrder inOrder = inOrder(userService,
            productService,
            userCouponService,
            couponService,
            orderService,
            orderService,
            balanceService,
            stockService,
            paymentService,
            rankService
        );

        inOrder.verify(userService, times(1)).getUser(criteria.getUserId());
        inOrder.verify(productService, times(1)).getOrderProducts(criteria.toProductCommand());
        inOrder.verify(userCouponService, times(1)).getUsableCoupon(criteria.toCouponCommand());
        inOrder.verify(couponService, times(1)).getCoupon(criteria.getCouponId());
        inOrder.verify(orderService, times(1)).createOrder(criteria.toOrderCommand(
            mockUsableCoupon.getUserCouponId(),
            mockCoupon.getCouponId(),
            mockOrderProducts
        ));
        inOrder.verify(balanceService, times(1)).useBalance(criteria.toBalanceCommand(
            mockOrder.getTotalPrice()
        ));
        inOrder.verify(userCouponService, times(1)).useUserCoupon(mockUsableCoupon.getUserCouponId());
        inOrder.verify(stockService, times(1)).deductStock(criteria.toStockCommand());
        inOrder.verify(paymentService, times(1)).pay(criteria.toPaymentCommand(mockOrder));
        inOrder.verify(orderService, times(1)).paidOrder(mockOrder.getOrderId());
        inOrder.verify(rankService, times(1)).createSellRank(criteria.toRankCommand(any()));
    }
}