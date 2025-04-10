package kr.hhplus.be.ecommerce.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ProductSellingStatus {

    HOLD("판매 보류"),
    SELLING("판매 중"),
    STOP_SELLING("판매 중지"),
    ;
    private final String description;

    private static final List<ProductSellingStatus> NON_SELLING_STATUSES = List.of(HOLD, STOP_SELLING);

    public boolean cannotSelling() {
        return NON_SELLING_STATUSES.contains(this);
    }

    public static List<ProductSellingStatus> forCelling() {
        return List.of(SELLING);
    }
}
