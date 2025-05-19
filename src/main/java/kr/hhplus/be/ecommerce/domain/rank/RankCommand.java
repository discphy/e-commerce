package kr.hhplus.be.ecommerce.domain.rank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankCommand {

    @Getter
    public static class CreateList {

        private final List<Create> ranks;

        private CreateList(List<Create> ranks) {
            this.ranks = ranks;
        }

        public static CreateList of(List<Create> ranks) {
            return new CreateList(ranks);
        }
    }

    @Getter
    public static class Create {

        private Long productId;
        private long score;
        private LocalDate rankDate;

        private Create(Long productId, long score, LocalDate rankDate) {
            this.productId = productId;
            this.rankDate = rankDate;
            this.score = score;
        }

        public static Create of(Long productId, long score, LocalDate rankDate) {
            return new Create(productId, score, rankDate);
        }
    }

    @Getter
    public static class Query {

        private final int top;
        private final RankKey target;
        private final RankKeys sources;

        private Query(int top, RankKey target, RankKeys sources) {
            this.top = top;
            this.target = target;
            this.sources = sources;
        }

        public static Query of(int top, RankKey target, RankKeys sources) {
            return new Query(top, target, sources);
        }
    }

    @Getter
    public static class PopularSellRank {

        private final int top;
        private final int days;
        private final LocalDate date;

        private PopularSellRank(int top, int days, LocalDate date) {
            this.top = top;
            this.days = days;
            this.date = date;
        }

        public static PopularSellRank of(int top, int days, LocalDate date) {
            return new PopularSellRank(top, days, date);
        }
    }
}
