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
    FINISHED("발급완료"),
    ;
    private final String description;

    private static final List<CouponStatus> CANNOT_PUBLISHABLE_STATUSES = List.of(REGISTERED, CANCELED, FINISHED);

    public boolean cannotPublishable() {
        return CANNOT_PUBLISHABLE_STATUSES.contains(this);
    }
}
