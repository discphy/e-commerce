package kr.hhplus.be.ecommerce.api.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.ecommerce.api.controller.balance.BalanceController;
import kr.hhplus.be.ecommerce.api.controller.coupon.CouponController;
import kr.hhplus.be.ecommerce.api.controller.order.OrderController;
import kr.hhplus.be.ecommerce.api.controller.product.ProductController;
import kr.hhplus.be.ecommerce.api.controller.rank.RankController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
}
