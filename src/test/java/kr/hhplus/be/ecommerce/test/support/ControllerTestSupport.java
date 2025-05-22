package kr.hhplus.be.ecommerce.test.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.ecommerce.application.order.OrderFacade;
import kr.hhplus.be.ecommerce.application.user.UserCouponFacade;
import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import kr.hhplus.be.ecommerce.interfaces.balance.api.BalanceController;
import kr.hhplus.be.ecommerce.interfaces.order.api.OrderController;
import kr.hhplus.be.ecommerce.interfaces.product.api.ProductController;
import kr.hhplus.be.ecommerce.interfaces.rank.api.RankController;
import kr.hhplus.be.ecommerce.interfaces.user.UserCouponController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    BalanceController.class,
    UserCouponController.class,
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
    protected OrderFacade orderFacade;

    @MockitoBean
    protected ProductService productService;

    @MockitoBean
    protected RankService rankService;

    @MockitoBean
    protected UserCouponFacade userCouponFacade;

}
