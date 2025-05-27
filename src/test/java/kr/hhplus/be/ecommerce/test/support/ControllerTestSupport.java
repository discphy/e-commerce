package kr.hhplus.be.ecommerce.test.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import kr.hhplus.be.ecommerce.interfaces.balance.api.BalanceController;
import kr.hhplus.be.ecommerce.interfaces.order.api.OrderController;
import kr.hhplus.be.ecommerce.interfaces.product.api.ProductController;
import kr.hhplus.be.ecommerce.interfaces.rank.api.RankController;
import kr.hhplus.be.ecommerce.interfaces.coupon.api.CouponController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    BalanceController.class,
    CouponController.class,
    OrderController.class,
    ProductController.class,
    RankController.class,
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected BalanceService balanceService;

    @MockitoBean
    protected OrderService orderService;

    @MockitoBean
    protected ProductService productService;

    @MockitoBean
    protected RankService rankService;

    @MockitoBean
    protected CouponService couponService;

}
