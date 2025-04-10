package kr.hhplus.be.ecommerce.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

    CARD("카드"),
    CASH("현금"),
    KAKAO_PAY("카카오페이"),
    TOSS_PAY("토스페이"),
    NAVER_PAY("네이버페이"),
    UNKNOWN("알 수 없음")
    ;

    private final String description;
}
