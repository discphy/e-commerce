package kr.hhplus.be.ecommerce.balance.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.ecommerce.balance.domain.BalanceService;
import kr.hhplus.be.ecommerce.balance.interfaces.api.BalanceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    BalanceController.class,
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected BalanceService balanceService;
}
