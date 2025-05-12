package kr.hhplus.be.ecommerce.interfaces.rank;

import kr.hhplus.be.ecommerce.application.rank.RankResult;
import kr.hhplus.be.ecommerce.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RankControllerTest extends ControllerTestSupport {

    @DisplayName("상위 상품 Top5 목록을 가져온다.")
    @Test
    void getRanks() throws Exception {
        // given
        when(rankFacade.getPopularProducts(any()))
            .thenReturn(RankResult.PopularProducts.of(
                List.of(
                    RankResult.PopularProduct.of(1L, "상품명", 300_000L)
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
            .andExpect(jsonPath("$.data.products[*].price").value(300000));
    }
}