package kr.hhplus.be.ecommerce.docs.balance;

import kr.hhplus.be.ecommerce.api.controller.balance.BalanceController;
import kr.hhplus.be.ecommerce.api.controller.balance.request.BalanceUpdateRequest;
import kr.hhplus.be.ecommerce.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BalanceControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new BalanceController();
    }

    @DisplayName("잔액 조회 API")
    @Test
    void getBalance() throws Exception {
        // when & then
        mockMvc.perform(
            get("/api/v1/users/{id}/balance", 1L)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("get-balance",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("사용자 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.amount").type(JsonFieldType.NUMBER).description("잔액")
                )
            ));
    }

    @DisplayName("잔액 충전 API")
    @Test
    void updateBalance() throws Exception {
        // given
        BalanceUpdateRequest request = BalanceUpdateRequest.of(10_000L);

        // when & then
        mockMvc.perform(
                post("/api/v1/users/{id}/balance", 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("update-balance",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("사용자 ID")
                ),
                requestFields(
                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("충전 금액")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지")
                )
            ));
    }
}