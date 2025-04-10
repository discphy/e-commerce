package kr.hhplus.be.ecommerce.docs.order;

import kr.hhplus.be.ecommerce.application.order.OrderFacade;
import kr.hhplus.be.ecommerce.docs.RestDocsSupport;
import kr.hhplus.be.ecommerce.interfaces.order.OrderController;
import kr.hhplus.be.ecommerce.interfaces.order.OrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerDocsTest extends RestDocsSupport {

    private final OrderFacade orderFacade = mock(OrderFacade.class);

    @Override
    protected Object initController() {
        return new OrderController(orderFacade);
    }

    @DisplayName("주문/결제 완료 API")
    @Test
    void createOrder() throws Exception {
        // given
        OrderRequest.Create request = OrderRequest.Create.of(
            1L,
            1L,
            List.of(
                OrderRequest.Product.of(1L, 2)
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
            .andDo(document("create-order",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("couponId").type(JsonFieldType.NUMBER).description("쿠푠 ID").optional(),
                    fieldWithPath("products[].id").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("products[].quantity").type(JsonFieldType.NUMBER).description("상품 수량")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지")
                )
            ));
    }
}