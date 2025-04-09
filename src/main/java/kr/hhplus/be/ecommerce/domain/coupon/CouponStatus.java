package kr.hhplus.be.ecommerce.domain.coupon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum CouponStatus {

    CANCELED("취소"),
    REGISTERED("등록"),
    PUBLISHABLE("발급가능"),
    ;
    private final String description;

    private static final List<CouponStatus> NON_PUBLISHABLE_STATUSES = List.of(REGISTERED, CANCELED);

    public boolean cannotPublishable() {
        return NON_PUBLISHABLE_STATUSES.contains(this);
    }
}
