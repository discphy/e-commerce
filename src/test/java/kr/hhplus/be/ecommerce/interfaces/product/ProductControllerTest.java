package kr.hhplus.be.ecommerce.interfaces.product;

import kr.hhplus.be.ecommerce.application.product.ProductResult;
import kr.hhplus.be.ecommerce.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends ControllerTestSupport {

    @DisplayName("상품 목록을 가져온다.")
    @Test
    void getProducts() throws Exception {
        // given
        when(productFacade.getProducts())
            .thenReturn(ProductResult.Products.of(
                List.of(
                    ProductResult.Product.of(1L, "상품명", 300_000L, 3)
                )
            ));

        // when & then
        mockMvc.perform(
                get("/api/v1/products")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.products[*].id").value(1))
            .andExpect(jsonPath("$.data.products[*].name").value("상품명"))
            .andExpect(jsonPath("$.data.products[*].price").value(300000))
            .andExpect(jsonPath("$.data.products[*].stock").value(3));
    }

    @DisplayName("상위 상품 Top5 목록을 가져온다.")
    @Test
    void getRanks() throws Exception {
        // given
        when(productFacade.getPopularProducts())
            .thenReturn(ProductResult.Products.of(
                List.of(
                    ProductResult.Product.of(1L, "상품명", 300_000L, 3)
                )
            ));

        // when & then
        mockMvc.perform(
                get("/api/v1/products/ranks")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.products[*].id").value(1))
            .andExpect(jsonPath("$.data.products[*].name").value("상품명"))
            .andExpect(jsonPath("$.data.products[*].price").value(300000))
            .andExpect(jsonPath("$.data.products[*].stock").value(3));
    }
}