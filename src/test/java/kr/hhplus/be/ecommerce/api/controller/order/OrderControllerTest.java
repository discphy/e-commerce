package kr.hhplus.be.ecommerce.api.controller.order;

import kr.hhplus.be.ecommerce.api.controller.order.request.OrderCreateRequest;
import kr.hhplus.be.ecommerce.api.controller.order.request.OrderProductRequest;
import kr.hhplus.be.ecommerce.api.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends ControllerTestSupport {

    @DisplayName("주문 시, 사용자 ID는 필수다.")
    @Test
    void createOrderWithoutUserId() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.of(
            null,
            1L,
            List.of(OrderProductRequest.of(1L, 1))
        );

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("사용자 ID는 필수 입니다."));
    }

    @DisplayName("주문 시, 상품목록은 1개 이상 있어야 한다.")
    @Test
    void createOrderWithEmptyProducts() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.of(
            1L,
            1L,
            List.of()
        );

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("상품 목록은 1개 이상이여야 합니다."));
    }

    @DisplayName("주문 시, 상품목록의 상품 ID는 필수이다.")
    @Test
    void createOrderWithoutProductId() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.of(
            1L,
            1L,
            List.of(
                OrderProductRequest.of(null, 1)
            )
        );

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("상품 ID는 필수입니다."));
    }

    @DisplayName("주문 시, 상품목록의 상품 구매 수량은 필수이다.")
    @Test
    void createOrderWithoutQuantity() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.of(
            1L,
            1L,
            List.of(
                OrderProductRequest.of(1L, null)
            )
        );

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("상품 구매 수량은 필수입니다."));
    }

    @DisplayName("주문 시, 상품목록의 상품 구매 수량은 양수여야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void createOrderWithNegativeOrZeroQuantity(int quantity) throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.of(
            1L,
            1L,
            List.of(
                OrderProductRequest.of(1L, quantity)
            )
        );

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("상품 구매 수량은 양수여야 합니다."));
    }

    @DisplayName("주문/결제를 한다.")
    @Test
    void createOrder() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.of(
            1L,
            1L,
            List.of(
                OrderProductRequest.of(1L, 2)
            )
        );

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"));
    }
}