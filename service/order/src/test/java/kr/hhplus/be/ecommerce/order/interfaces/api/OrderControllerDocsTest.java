package kr.hhplus.be.ecommerce.order.interfaces.api;

import kr.hhplus.be.ecommerce.order.domain.OrderInfo;
import kr.hhplus.be.ecommerce.order.domain.OrderService;
import kr.hhplus.be.ecommerce.order.domain.OrderStatus;
import kr.hhplus.be.ecommerce.support.restdocs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerDocsTest extends RestDocsSupport {

    private final OrderService orderService = mock(OrderService.class);

    @Override
    protected Object initController() {
        return new OrderController(orderService);
    }

    @DisplayName("주문/결제 완료 API")
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
                .userId(1L)
                .userCouponId(1L)
                .totalPrice(10000L)
                .discountPrice(2000L)
                .status(OrderStatus.CREATED)
                .build());

        // when & then
        mockMvc.perform(
                post("/api/v1/orders")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("create-order",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("userCouponId").type(JsonFieldType.NUMBER).description("사용자 쿠푠 ID").optional(),
                    fieldWithPath("products[].id").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("products[].quantity").type(JsonFieldType.NUMBER).description("상품 수량")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                    fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("data.userCouponId").type(JsonFieldType.NUMBER).description("사용자 쿠폰 ID"),
                    fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description("주문 금액"),
                    fieldWithPath("data.discountPrice").type(JsonFieldType.NUMBER).description("할인 금액"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING).description("주문 상태")
                )
            ));
    }

    @DisplayName("주문을 조회한다.")
    @Test
    void getOrder() throws Exception {
        // given
        Long orderId = 1L;

        when(orderService.getOrder(orderId))
            .thenReturn(OrderInfo.Order.builder()
                .orderId(orderId)
                .userId(1L)
                .userCouponId(1L)
                .totalPrice(10000L)
                .discountPrice(2000L)
                .status(OrderStatus.CREATED)
                .build());

        // when & then
        mockMvc.perform(
                get("/api/v1/orders/{orderId}", orderId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("get-order",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                    fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("data.userCouponId").type(JsonFieldType.NUMBER).description("사용자 쿠폰 ID"),
                    fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description("주문 금액"),
                    fieldWithPath("data.discountPrice").type(JsonFieldType.NUMBER).description("할인 금액"),
                    fieldWithPath("data.status").type(JsonFieldType.STRING).description("주문 상태")
                )
            ));
    }
}