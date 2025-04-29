package kr.hhplus.be.ecommerce.application.rank;

import kr.hhplus.be.ecommerce.domain.order.Order;
import kr.hhplus.be.ecommerce.domain.order.OrderProduct;
import kr.hhplus.be.ecommerce.domain.order.OrderRepository;
import kr.hhplus.be.ecommerce.domain.rank.RankCommand;
import kr.hhplus.be.ecommerce.domain.rank.RankInfo;
import kr.hhplus.be.ecommerce.domain.rank.RankRepository;
import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class RankFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private RankFacade rankFacade;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RankRepository rankRepository;

    @DisplayName("일별 랭킹을 생성한다.")
    @Test
    void createDailyRankAt() {
        // given
        Order order1 = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품1", 10_000L, 2),
            OrderProduct.create(2L, "상품2", 20_000L, 3)
        ));
        Order order2 = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(1L, "상품1", 10_000L, 2),
            OrderProduct.create(3L, "상품3", 30_000L, 4)
        ));
        Order order3 = Order.create(1L, 1L, 0.1, List.of(
            OrderProduct.create(2L, "상품2", 20_000L, 3),
            OrderProduct.create(3L, "상품3", 30_000L, 4)
        ));

        List.of(order1, order2, order3)
            .forEach(order -> {
                order.paid(LocalDateTime.of(2025, 4, 22, 12, 0, 0));
                orderRepository.save(order);
            });


        LocalDate date = LocalDate.of(2025, 4, 23);

        // when
        rankFacade.createDailyRankAt(date);

        // then
        RankCommand.PopularSellRank command = RankCommand.PopularSellRank.of(3, LocalDate.of(2025, 4, 22), LocalDate.of(2025, 4, 23));
        List<RankInfo.PopularProduct> result = rankRepository.findPopularSellRanks(command);
        assertThat(result).hasSize(3)
            .extracting(RankInfo.PopularProduct::getProductId)
            .containsExactly(3L, 2L, 1L);
    }

}