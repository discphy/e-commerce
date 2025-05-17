package kr.hhplus.be.ecommerce.domain.rank;

import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class RankServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private RankService rankService;

    @Autowired
    private RankRepository rankRepository;

    @DisplayName("판매 랭크를 생성한다.")
    @Test
    void createSellRank() {
        // given
        RankCommand.CreateList command = RankCommand.CreateList.of(
            List.of(
                RankCommand.Create.of(1L, 1L, LocalDate.of(2025, 4, 23)),
                RankCommand.Create.of(2L, 2L, LocalDate.of(2025, 4, 23)),
                RankCommand.Create.of(3L, 3L, LocalDate.of(2025, 4, 23))
            )
        );

        // when
        List<Rank> results = rankService.createSellRank(command);

        // then
        assertThat(results).hasSize(3)
            .extracting(Rank::getProductId)
            .containsExactly(1L, 2L, 3L);
    }

    @DisplayName("인기 판매 랭크를 조회한다.")
    @Test
    void getPopularSellRank() {
        // given
        Rank rank1 = Rank.createSell(1L, LocalDate.of(2025, 4, 30), 89L);
        Rank rank2 = Rank.createSell(2L, LocalDate.of(2025, 4, 29), 60L);
        Rank rank3 = Rank.createSell(3L, LocalDate.of(2025, 4, 29), 51L);
        Rank rank4 = Rank.createSell(4L, LocalDate.of(2025, 4, 29), 41L);
        Rank rank5 = Rank.createSell(5L, LocalDate.of(2025, 4, 29), 33L);

        List<Rank> ranks = List.of(
            rank1,
            rank2,
            rank3,
            rank4,
            rank5,
            Rank.createSell(6L, LocalDate.of(2025, 4, 28), 10L), // 순위 밀림
            Rank.createSell(7L, LocalDate.of(2025, 4, 26), 34L) // 날짜 미해당
        );

        ranks.forEach(rankRepository::save);

        RankCommand.PopularSellRank command = RankCommand.PopularSellRank.of(5, 3, LocalDate.of(2025, 4, 30));

        // when
        RankInfo.PopularProducts result = rankService.getPopularSellRank(command);

        // then
        assertThat(result.getProducts()).hasSize(5)
            .extracting(RankInfo.PopularProduct::getProductId)
            .containsExactly(
                rank1.getProductId(),
                rank2.getProductId(),
                rank3.getProductId(),
                rank4.getProductId(),
                rank5.getProductId()
            );
    }
}