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
    public static class PopularSellRank {

        private final int top;
        private final LocalDate startDate;
        private final LocalDate endDate;

        private PopularSellRank(int top, LocalDate startDate, LocalDate endDate) {
            this.top = top;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public static PopularSellRank of(int top, LocalDate startDate, LocalDate endDate) {
            return new PopularSellRank(top, startDate, endDate);
        }
    }
}
