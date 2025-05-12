package kr.hhplus.be.ecommerce.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.ecommerce.application.balance.BalanceFacade;
import kr.hhplus.be.ecommerce.application.order.OrderFacade;
import kr.hhplus.be.ecommerce.application.product.ProductFacade;
import kr.hhplus.be.ecommerce.application.rank.RankFacade;
import kr.hhplus.be.ecommerce.application.user.UserCouponFacade;
import kr.hhplus.be.ecommerce.interfaces.balance.BalanceController;
import kr.hhplus.be.ecommerce.interfaces.order.OrderController;
import kr.hhplus.be.ecommerce.interfaces.product.ProductController;
import kr.hhplus.be.ecommerce.interfaces.rank.RankController;
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
    protected BalanceFacade balanceFacade;

    @MockitoBean
    protected OrderFacade orderFacade;

    @MockitoBean
    protected ProductFacade productFacade;

    @MockitoBean
    protected RankFacade rankFacade;

    @MockitoBean
    protected UserCouponFacade userCouponFacade;

}
