package kr.hhplus.be.ecommerce.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum UserCouponUsedStatus {

    USED("사용완료"),
    UNUSED("미사용"),
    ;

    private final String description;

    private static final List<UserCouponUsedStatus> NON_USABLE = List.of(USED);

    public boolean cannotUsable() {
        return NON_USABLE.contains(this);
    }

    public static List<UserCouponUsedStatus> forUsable() {
        return List.of(UNUSED);
    }
}
