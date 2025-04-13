package kr.hhplus.be.ecommerce.docs.product;

import kr.hhplus.be.ecommerce.application.product.ProductFacade;
import kr.hhplus.be.ecommerce.application.product.ProductResult;
import kr.hhplus.be.ecommerce.interfaces.product.ProductController;
import kr.hhplus.be.ecommerce.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerDocsTest extends RestDocsSupport {

    private final ProductFacade productFacade = mock(ProductFacade.class);

    @Override
    protected Object initController() {
        return new ProductController(productFacade);
    }

    @DisplayName("상품 목록 조회 API")
    @Test
    void getProducts() throws Exception {
        // given
        when(productFacade.getProducts())
            .thenReturn(ProductResult.Products.of(
                List.of(
                    ProductResult.Product.of(1L, "상품명", 300_000L, 3)
                )
            ));

        // when & then
        mockMvc.perform(
                get("/api/v1/products")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("get-products",
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

    @DisplayName("상위 상품 Top5 목록 조회 API")
    @Test
    void getRanks() throws Exception {
        // given
        when(productFacade.getPopularProducts())
            .thenReturn(ProductResult.Products.of(
                List.of(
                    ProductResult.Product.of(1L, "상품명", 300_000L, 3)
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
                    fieldWithPath("data.products[].price").type(JsonFieldType.NUMBER).description("가격"),
                    fieldWithPath("data.products[].stock").type(JsonFieldType.NUMBER).description("재고 수")
                )
            ));
    }
}