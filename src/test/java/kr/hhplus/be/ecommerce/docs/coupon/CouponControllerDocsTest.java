package kr.hhplus.be.ecommerce.docs.coupon;

import kr.hhplus.be.ecommerce.domain.coupon.CouponInfo;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.interfaces.coupon.api.CouponController;
import kr.hhplus.be.ecommerce.interfaces.coupon.api.CouponRequest;
import kr.hhplus.be.ecommerce.test.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponControllerDocsTest extends RestDocsSupport {

    private final CouponService couponService = mock(CouponService.class);

    @Override
    protected Object initController() {
        return new CouponController(couponService);
    }

    @DisplayName("쿠폰 목록 조회 API")
    @Test
    void getCoupons() throws Exception {
        // given
        when(couponService.getUserCoupons(1L))
            .thenReturn(CouponInfo.Coupons.of(
                List.of(
                    CouponInfo.Coupon.builder()
                        .userCouponId(1L)
                        .couponName("쿠폰명")
                        .discountRate(0.1)
                        .build(),
                    CouponInfo.Coupon.builder()
                        .userCouponId(2L)
                        .couponName("쿠폰명2")
                        .discountRate(0.2)
                        .build()
                )
            ));

        // when & then
        mockMvc.perform(
                get("/api/v1/users/{id}/coupons", 1L)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("get-coupons",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("사용자 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.coupons[]").type(JsonFieldType.ARRAY).description("쿠폰 목록"),
                    fieldWithPath("data.coupons[].id").type(JsonFieldType.NUMBER).description("쿠폰 ID"),
                    fieldWithPath("data.coupons[].name").type(JsonFieldType.STRING).description("쿠폰 이름"),
                    fieldWithPath("data.coupons[].discountRate").type(JsonFieldType.NUMBER).description("쿠폰 할인율")
                )
            ));
    }

    @DisplayName("쿠폰 발급 API")
    @Test
    void publishCoupon() throws Exception {
        // given
        CouponRequest.Publish request = CouponRequest.Publish.of(1L);

        // when & then
        mockMvc.perform(
                post("/api/v1/users/{id}/coupons/publish", 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("publish-coupon",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("사용자 ID")
                ),
                requestFields(
                    fieldWithPath("couponId").type(JsonFieldType.NUMBER).description("쿠폰 ID")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지")
                )
            ));
    }
}