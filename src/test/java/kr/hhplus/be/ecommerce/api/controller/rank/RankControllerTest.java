package kr.hhplus.be.ecommerce.api.controller.rank;

import kr.hhplus.be.ecommerce.api.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RankControllerTest extends ControllerTestSupport {

    @DisplayName("상위 상품 Top5 목록을 가져온다.")
    @Test
    void getRanks() throws Exception {
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