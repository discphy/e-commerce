package kr.hhplus.be.ecommerce.domain.rank;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_rank", indexes = {
    @Index(name = "idx_rank_type_date", columnList = "rank_type, rank_date")
})
public class Rank {

    @Id
    @Column(name = "rank_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private LocalDate rankDate;

    @Enumerated(EnumType.STRING)
    private RankType rankType;

    private long score;

    @Builder
    private Rank(Long id, Long productId, LocalDate rankDate, RankType rankType, long score) {
        this.id = id;
        this.productId = productId;
        this.rankDate = rankDate;
        this.rankType = rankType;
        this.score = score;
    }

    public static Rank createSell(Long productId, LocalDate rankDate, long score) {
        validateProduct(productId);
        validateDate(rankDate);

        return Rank.builder()
            .productId(productId)
            .rankDate(rankDate)
            .rankType(RankType.SELL)
            .score(score)
            .build();
    }

    private static void validateProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("상품이 존재하지 않습니다.");
        }
    }

    private static void validateDate(LocalDate rankDate) {
        if (rankDate == null) {
            throw new IllegalArgumentException("날짜가 존재하지 않습니다.");
        }
    }
}
