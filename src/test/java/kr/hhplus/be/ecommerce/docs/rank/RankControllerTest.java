package kr.hhplus.be.ecommerce.docs.rank;

import kr.hhplus.be.ecommerce.api.controller.rank.RankController;
import kr.hhplus.be.ecommerce.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RankControllerTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new RankController();
    }

    @DisplayName("상위 상품 Top5 목록 조회 API")
    @Test
    void getRanks() throws Exception {
        // when & then
        mockMvc.perform(
                get("/api/v1/products/ranks")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("get-ranks",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.products[]").type(JsonFieldType.ARRAY).description("상품 목록"),
                    fieldWithPath("data.products[].id").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data.products[].name").type(JsonFieldType.STRING).description("상품 이름"),
                    fieldWithPath("data.products[].price").type(JsonFieldType.NUMBER).description("가격"),
                    fieldWithPath("data.products[].stock").type(JsonFieldType.NUMBER).description("재고 수")
                )
            ));
    }
}