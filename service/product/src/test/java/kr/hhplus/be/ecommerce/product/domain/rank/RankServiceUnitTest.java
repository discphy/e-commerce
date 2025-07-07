package kr.hhplus.be.ecommerce.product.domain.rank;

import kr.hhplus.be.ecommerce.product.domain.product.Product;
import kr.hhplus.be.ecommerce.product.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RankServiceUnitTest extends MockTestSupport {

    @InjectMocks
    private RankService rankService;

    @Mock
    private RankRepository rankRepository;

    @Mock
    private RankEventPublisher rankEventPublisher;

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

        when(rankRepository.save(any()))
            .thenReturn(Rank.createSell(1L, LocalDate.of(2025, 4, 23), 1L))
            .thenReturn(Rank.createSell(2L, LocalDate.of(2025, 4, 23), 2L))
            .thenReturn(Rank.createSell(3L, LocalDate.of(2025, 4, 23), 3L));

        // when
        rankService.createSellRank(command);

        // then
        verify(rankRepository, times(3)).save(any());
        verify(rankEventPublisher).created(any(RankEvent.Created.class));
    }

    @DisplayName("인기 판매 랭크를 조회한다.")
    @Test
    void getPopularProducts() {
        // given
        List<RankInfo.ProductScore> productScores = List.of(
            RankInfo.ProductScore.of(1L, 120L),  // 1등 상품
            RankInfo.ProductScore.of(2L, 95L),   // 2등 상품
            RankInfo.ProductScore.of(3L, 87L),   // 3등 상품
            RankInfo.ProductScore.of(4L, 76L),   // 4등 상품
            RankInfo.ProductScore.of(5L, 65L)   // 5등 상품
        );

        when(rankRepository.findProductScores(any()))
            .thenReturn(productScores);

        when(rankRepository.findProductById(anyLong()))
            .thenReturn(Product.builder().id(1L).build())
            .thenReturn(Product.builder().id(2L).build())
            .thenReturn(Product.builder().id(3L).build())
            .thenReturn(Product.builder().id(4L).build())
            .thenReturn(Product.builder().id(5L).build());

        RankCommand.PopularProducts command = RankCommand.PopularProducts.of(
            5,
            7,
            LocalDate.of(2025, 4, 30)
        );

        // when
        RankInfo.PopularProducts result = rankService.getPopularProducts(command);

        // then
        assertThat(result.getProducts()).hasSize(5)
            .extracting(RankInfo.PopularProduct::getProductId)
            .containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @DisplayName("일일 판매 랭크를 영속화 한다.")
    @Test
    void persistDailyRank() {
        // given
        LocalDate date = LocalDate.of(2025, 5, 16);

        List<RankInfo.ProductScore> products = List.of(
            RankInfo.ProductScore.of(1L, 100L),
            RankInfo.ProductScore.of(2L, 90L),
            RankInfo.ProductScore.of(3L, 80L)
        );

        when(rankRepository.findDailyRank(any()))
            .thenReturn(products);

        // when
        rankService.persistDailyRank(date);

        // then
        verify(rankRepository, times(1)).saveAll(any());
        verify(rankRepository, times(1)).delete(any());
    }

    @DisplayName("일일 판매 랭크가 비어있으면 영속화 하지 않는다.")
    @Test
    void persistDailyRankWithEmpty() {
        // given
        LocalDate date = LocalDate.of(2025, 5, 16);

        List<RankInfo.ProductScore> products = List.of();

        when(rankRepository.findDailyRank(any()))
            .thenReturn(products);

        // when
        rankService.persistDailyRank(date);

        // then
        verify(rankRepository, never()).saveAll(any());
        verify(rankRepository, never()).delete(any());
    }
}