package kr.hhplus.be.ecommerce.application.rank;

import kr.hhplus.be.ecommerce.domain.order.Order;
import kr.hhplus.be.ecommerce.domain.order.OrderProduct;
import kr.hhplus.be.ecommerce.domain.order.OrderRepository;
import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import kr.hhplus.be.ecommerce.domain.rank.Rank;
import kr.hhplus.be.ecommerce.domain.rank.RankCommand;
import kr.hhplus.be.ecommerce.domain.rank.RankInfo;
import kr.hhplus.be.ecommerce.domain.rank.RankRepository;
import kr.hhplus.be.ecommerce.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private ProductRepository productRepository;

    private Product product1;

    private Product product2;

    private Product product3;

    @BeforeEach
    void setUp() {
        product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        product2 = Product.create("상품명2", 2_000L, ProductSellingStatus.SELLING);
        product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.STOP_SELLING);

        List.of(product1, product2, product3)
            .forEach(productRepository::save);
    }

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

    @DisplayName("상위 상품을 조회한다.")
    @Test
    void getPopularProducts() {
        // given
        List<Rank> ranks = List.of(
            Rank.createSell(product1.getId(), LocalDate.now().minusDays(1), 10L),
            Rank.createSell(product2.getId(), LocalDate.now().minusDays(1), 34L),
            Rank.createSell(product3.getId(), LocalDate.now().minusDays(2), 42L)
        );

        ranks.forEach(rankRepository::save);

        // when
        RankResult.PopularProducts products = rankFacade.getPopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        // then
        assertThat(products.getProducts()).hasSize(3)
            .extracting("productId")
            .containsExactly(product3.getId(), product2.getId(), product1.getId());
    }
}