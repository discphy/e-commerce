package kr.hhplus.be.ecommerce.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    READY("결제 준비"),
    WAITING("결제 대기"),
    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    CANCELED("결제 취소"),
    ;

    private final String description;

    private static final List<PaymentStatus> CANNOT_PAYABLE_STATUSES = List.of(COMPLETED, FAILED, CANCELED);

    public boolean cannotPayable() {
        return CANNOT_PAYABLE_STATUSES.contains(this);
    }
}
