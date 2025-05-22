package kr.hhplus.be.ecommerce.domain.rank;

import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
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

    @Autowired
    private ProductRepository productRepository;

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
    void getPopularProducts() {
        // given
        Product product1 = Product.create("항해 블랙뱃지", 100_000L, ProductSellingStatus.SELLING);
        Product product2 = Product.create("항해 화이트뱃지", 200_000L, ProductSellingStatus.SELLING);
        Product product3 = Product.create("항해 블루뱃지", 300_000L, ProductSellingStatus.SELLING);
        Product product4 = Product.create("항해 레드뱃지", 400_000L, ProductSellingStatus.SELLING);
        Product product5 = Product.create("항해 그린뱃지", 500_000L, ProductSellingStatus.SELLING);
        Product product6 = Product.create("항해 옐로우뱃지", 600_000L, ProductSellingStatus.SELLING);
        Product product7 = Product.create("항해 퍼플뱃지", 700_000L, ProductSellingStatus.SELLING);

        List.of(product1, product2, product3, product4, product5, product6, product7).forEach(productRepository::save);

        Rank rank1 = Rank.createSell(product1.getId(), LocalDate.of(2025, 4, 30), 89L);
        Rank rank2 = Rank.createSell(product2.getId(), LocalDate.of(2025, 4, 29), 60L);
        Rank rank3 = Rank.createSell(product3.getId(), LocalDate.of(2025, 4, 29), 51L);
        Rank rank4 = Rank.createSell(product4.getId(), LocalDate.of(2025, 4, 29), 41L);
        Rank rank5 = Rank.createSell(product5.getId(), LocalDate.of(2025, 4, 29), 33L);

        List<Rank> ranks = List.of(
            rank1,
            rank2,
            rank3,
            rank4,
            rank5,
            Rank.createSell(product6.getId(), LocalDate.of(2025, 4, 28), 10L), // 순위 밀림
            Rank.createSell(product7.getId(), LocalDate.of(2025, 4, 26), 34L) // 날짜 미해당
        );

        ranks.forEach(rankRepository::save);

        RankCommand.PopularProducts command = RankCommand.PopularProducts.of(5, 3, LocalDate.of(2025, 4, 30));

        // when
        RankInfo.PopularProducts result = rankService.getPopularProducts(command);

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

    @DisplayName("일일 판매 랭크를 영속화 한다.")
    @Test
    void persistDailyRank() {
        // given
        LocalDate date = LocalDate.of(2025, 5, 16);

        List<Rank> ranks = List.of(
            Rank.createSell(1L, date, 100L),
            Rank.createSell(2L, date, 90L),
            Rank.createSell(3L, date, 80L)
        );

        ranks.forEach(rankRepository::save);

        // when
        rankService.persistDailyRank(date);

        // then
        List<Rank> results = rankRepository.findBy(RankType.SELL, date);
        assertThat(results).hasSize(3)
            .extracting(Rank::getProductId)
            .containsExactlyInAnyOrder(1L, 2L, 3L);
        List<RankInfo.ProductScore> deleted = rankRepository.findDailyRank(RankKey.ofDate(RankType.SELL, date));
        assertThat(deleted).isEmpty();
    }

    @DisplayName("일일 판매 랭크가 비어있으면 영속화 하지 않는다.")
    @Test
    void persistDailyRankWithEmpty() {
        // given
        LocalDate date = LocalDate.of(2025, 5, 16);

        // when
        rankService.persistDailyRank(date);

        // then
        List<Rank> results = rankRepository.findBy(RankType.SELL, date);
        assertThat(results).isEmpty();
    }
}