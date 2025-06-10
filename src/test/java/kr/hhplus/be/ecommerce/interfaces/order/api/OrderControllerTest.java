package kr.hhplus.be.ecommerce.interfaces.order.api;

import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.test.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends ControllerTestSupport {

    @DisplayName("주문 시, 사용자 ID는 필수다.")
    @Test
    void createOrderWithoutUserId() throws Exception {
        // given
        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            null,
            1L,
            List.of(OrderRequest.OrderProduct.of(1L, 1))
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
        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
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
        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            1L,
            1L,
            List.of(
                OrderRequest.OrderProduct.of(null, 1)
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
        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            1L,
            1L,
            List.of(
                OrderRequest.OrderProduct.of(1L, null)
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
        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            1L,
            1L,
            List.of(
                OrderRequest.OrderProduct.of(1L, quantity)
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
        OrderRequest.OrderPayment request = OrderRequest.OrderPayment.of(
            1L,
            1L,
            List.of(
                OrderRequest.OrderProduct.of(1L, 2)
            )
        );

        when(orderService.createOrder(any()))
            .thenReturn(OrderInfo.Order.builder()
                .orderId(1L)
                .totalPrice(10000L)
                .discountPrice(2000L)
                .build());

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.orderId").value(1L))
            .andExpect(jsonPath("$.data.totalPrice").value(10000L))
            .andExpect(jsonPath("$.data.discountPrice").value(2000L))
        ;
    }

    @DisplayName("주문을 조회한다.")
    @Test
    void getOrder() throws Exception {
        // given
        Long orderId = 1L;

        when(orderService.getOrder(orderId))
            .thenReturn(OrderInfo.Order.builder()
                .orderId(orderId)
                .totalPrice(10000L)
                .discountPrice(2000L)
                .build());

        // when & then
        mockMvc.perform(
                get("/api/v1/orders/{orderId}", orderId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.orderId").value(orderId))
            .andExpect(jsonPath("$.data.totalPrice").value(10000L))
            .andExpect(jsonPath("$.data.discountPrice").value(2000L))
        ;
    }
}