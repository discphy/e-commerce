package kr.hhplus.be.ecommerce.product.domain.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class RankEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Created {

        private List<Ranked> ranks;

        public static Created of(List<Rank> ranks) {
            return new Created(
                ranks.stream()
                    .map(Ranked::of)
                    .toList()
            );
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ranked {

        private Long id;
        private Long productId;
        private LocalDate rankDate;
        private RankType rankType;
        private long score;

        public static Ranked of(Rank rank) {
            return new Ranked(
                rank.getId(),
                rank.getProductId(),
                rank.getRankDate(),
                rank.getRankType(),
                rank.getScore()
            );
        }
    }
}
