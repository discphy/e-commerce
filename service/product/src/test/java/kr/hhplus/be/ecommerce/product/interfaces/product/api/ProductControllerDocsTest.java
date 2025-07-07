package kr.hhplus.be.ecommerce.product.interfaces.product.api;

import kr.hhplus.be.ecommerce.product.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.product.domain.product.ProductService;
import kr.hhplus.be.ecommerce.product.interfaces.api.ProductController;
import kr.hhplus.be.ecommerce.support.restdocs.RestDocsSupport;
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

    private final ProductService productService = mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @DisplayName("상품 목록 조회 API")
    @Test
    void getProducts() throws Exception {
        // given
        when(productService.getSellingProducts())
            .thenReturn(
                ProductInfo.Products.of(
                    List.of(
                        ProductInfo.Product.of(1L, "상품명", 300_000L, 3)
                    )
                )
            );

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
}