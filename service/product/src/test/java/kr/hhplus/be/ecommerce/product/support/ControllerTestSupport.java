package kr.hhplus.be.ecommerce.product.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.ecommerce.product.domain.product.ProductService;
import kr.hhplus.be.ecommerce.product.domain.rank.RankService;
import kr.hhplus.be.ecommerce.product.interfaces.api.ProductController;
import kr.hhplus.be.ecommerce.product.interfaces.api.RankController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    ProductController.class,
    RankController.class,
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected ProductService productService;

    @MockitoBean
    protected RankService rankService;
}
