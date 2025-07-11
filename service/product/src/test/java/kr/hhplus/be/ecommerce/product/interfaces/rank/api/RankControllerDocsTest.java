package kr.hhplus.be.ecommerce.product.interfaces.rank.api;

import kr.hhplus.be.ecommerce.product.domain.rank.RankInfo;
import kr.hhplus.be.ecommerce.product.domain.rank.RankService;
import kr.hhplus.be.ecommerce.product.interfaces.api.RankController;
import kr.hhplus.be.ecommerce.support.restdocs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RankControllerDocsTest extends RestDocsSupport {

    private final RankService rankService = mock(RankService.class);

    @Override
    protected Object initController() {
        return new RankController(rankService);
    }

    @DisplayName("상위 상품 Top5 목록 조회 API")
    @Test
    void getRanks() throws Exception {
        // given
        when(rankService.cachedPopularProducts(any()))
            .thenReturn(RankInfo.PopularProducts.of(
                List.of(
                    RankInfo.PopularProduct.builder()
                        .productId(1L)
                        .productName("상품명")
                        .productPrice(300_000L)
                        .build()
                )
            ));

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
                    fieldWithPath("data.products[].price").type(JsonFieldType.NUMBER).description("가격")
                )
            ));
    }
}