package kr.hhplus.be.ecommerce.api.controller.coupon;

import kr.hhplus.be.ecommerce.api.controller.coupon.request.CouponPublishRequest;
import kr.hhplus.be.ecommerce.api.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponControllerTest extends ControllerTestSupport {

    @DisplayName("보유한 쿠폰 목록을 가져온다.")
    @Test
    void getCoupons() throws Exception {
        // when & then
        mockMvc.perform(
            get("/api/v1/users/{id}/coupons", 1L)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.coupons[*].id").value(1))
            .andExpect(jsonPath("$.data.coupons[*].name").value("쿠폰명"))
            .andExpect(jsonPath("$.data.coupons[*].discountRate").value(0.1))
            .andExpect(jsonPath("$.data.coupons[*].expiredAt").value("2025-04-01 12:00:00"));
    }

    @DisplayName("쿠폰을 발급 시, 쿠픈 ID는 필수이다.")
    @Test
    void publishCouponWithoutCouponId() throws Exception {
        // given
        CouponPublishRequest request = new CouponPublishRequest();

        // when & then
        mockMvc.perform(
            post("/api/v1/users/{id}/coupons", 1L)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("쿠폰 ID는 필수입니다."));
    }

    @DisplayName("쿠폰을 발급한다.")
    @Test
    void publishCoupon() throws Exception {
        // given
        CouponPublishRequest request = CouponPublishRequest.of(1L);

        // when & then
        mockMvc.perform(
                post("/api/v1/users/{id}/coupons", 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"));
    }
}