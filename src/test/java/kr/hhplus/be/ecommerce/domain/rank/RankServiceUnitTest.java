package kr.hhplus.be.ecommerce.domain.rank;

import kr.hhplus.be.ecommerce.support.MockTestSupport;
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
    }

    @DisplayName("인기 판매 랭크를 조회한다.")
    @Test
    void getPopularSellRank() {
        // given
        List<RankInfo.PopularProduct> popularProducts = List.of(
            RankInfo.PopularProduct.of(1L, 120L),  // 1등 상품
            RankInfo.PopularProduct.of(2L, 95L),   // 2등 상품
            RankInfo.PopularProduct.of(3L, 87L),   // 3등 상품
            RankInfo.PopularProduct.of(4L, 76L),   // 4등 상품
            RankInfo.PopularProduct.of(5L, 65L)   // 5등 상품
        );

        when(rankRepository.findPopularSellRanks(any()))
            .thenReturn(popularProducts);

        RankCommand.PopularSellRank command = RankCommand.PopularSellRank.of(
            5,
            7,
            LocalDate.of(2025, 4, 30)
        );

        // when
        RankInfo.PopularProducts result = rankService.getPopularSellRank(command);

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

        List<RankInfo.PopularProduct> products = List.of(
            RankInfo.PopularProduct.of(1L, 100L),
            RankInfo.PopularProduct.of(2L, 90L),
            RankInfo.PopularProduct.of(3L, 80L)
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

        List<RankInfo.PopularProduct> products = List.of();

        when(rankRepository.findDailyRank(any()))
            .thenReturn(products);

        // when
        rankService.persistDailyRank(date);

        // then
        verify(rankRepository, never()).saveAll(any());
        verify(rankRepository, never()).delete(any());
    }
}