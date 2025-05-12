package kr.hhplus.be.ecommerce.application.rank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankCriteria {

    @Getter
    public static class PopularProducts {

        private final int top;
        private final int days;

        private PopularProducts(int top, int days) {
            this.top = top;
            this.days = days;
        }

        public static PopularProducts of(int top, int days) {
            return new PopularProducts(top, days);
        }

        public static PopularProducts ofTop5Days3() {
            return new PopularProducts(RankConstant.TOP_5, RankConstant.DAYS_3);
        }
    }
}
