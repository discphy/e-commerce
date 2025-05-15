package kr.hhplus.be.ecommerce.application.rank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

        public static PopularProducts ofTop5Days3() {
            return new PopularProducts(RankConstant.TOP_5, RankConstant.DAYS_3);
        }
    }

    @Getter
    public static class PersistDailyRank {

        private final LocalDate date;

        private PersistDailyRank(LocalDate date) {
            this.date = date;
        }

        public static PersistDailyRank ofBeforeDays(LocalDate date) {
            return new PersistDailyRank(date.minusDays(RankConstant.PERSIST_DAYS));
        }
    }
}
