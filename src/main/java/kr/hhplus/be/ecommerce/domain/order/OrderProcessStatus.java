package kr.hhplus.be.ecommerce.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderProcessStatus {

    SUCCESS("성공"),
    FAILED("실패"),
    PENDING("대기"),
    ;

    private final String description;
}
