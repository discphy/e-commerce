package kr.hhplus.be.ecommerce.application.rank;

import kr.hhplus.be.ecommerce.domain.order.Order;
import kr.hhplus.be.ecommerce.domain.order.OrderProduct;
import kr.hhplus.be.ecommerce.domain.order.OrderRepository;
import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import kr.hhplus.be.ecommerce.domain.rank.*;
import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private Product product1;

    private Product product2;

    private Product product3;

    private Product product4;

    private Product product5;

    @BeforeEach
    void setUp() {
        product1 = Product.create("상품명1", 1_000L, ProductSellingStatus.SELLING);
        product2 = Product.create("상품명2", 2_000L, ProductSellingStatus.SELLING);
        product3 = Product.create("상품명3", 3_000L, ProductSellingStatus.STOP_SELLING);
        product4 = Product.create("상품명4", 4_000L, ProductSellingStatus.SELLING);
        product5 = Product.create("상품명5", 5_000L, ProductSellingStatus.SELLING);

        List.of(product1, product2, product3, product4, product5)
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
        RankKey target = RankKey.ofDays(RankType.SELL, 3);
        RankKeys rankKeys = RankKeys.ofDaysWithDate(RankType.SELL, 1, LocalDate.of(2025, 4, 23));
        RankCommand.Query command = RankCommand.Query.of(3, target, rankKeys);
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

    @DisplayName("인기 상품을 조회하고, 캐시를 갱신한다.")
    @Test
    void updatePopularProducts() {
        // given
        LocalDate today = LocalDate.now();
        LocalDate before1Days = today.minusDays(1);
        LocalDate before2Days = today.minusDays(2);

        List<Rank> ranks = List.of(
            Rank.createSell(product1.getId(), today, 1L),
            Rank.createSell(product2.getId(), today, 2L),
            Rank.createSell(product3.getId(), today, 3L),
            Rank.createSell(product4.getId(), today, 4L),
            Rank.createSell(product5.getId(), today, 5L),

            Rank.createSell(product1.getId(), before1Days, 5L),
            Rank.createSell(product2.getId(), before1Days, 3L),
            Rank.createSell(product3.getId(), before1Days, 2L),
            Rank.createSell(product4.getId(), before1Days, 1L),
            Rank.createSell(product5.getId(), before1Days, 4L),

            Rank.createSell(product1.getId(), before2Days, 4L),
            Rank.createSell(product2.getId(), before2Days, 3L),
            Rank.createSell(product3.getId(), before2Days, 2L),
            Rank.createSell(product4.getId(), before2Days, 1L),
            Rank.createSell(product5.getId(), before2Days, 0L)
        );

        ranks.forEach(rankRepository::save);

        // when
        RankResult.PopularProducts products = rankFacade.updatePopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        // then
        RankKey rankKey = RankKey.ofDays(RankType.SELL, 3);
        Long size = redisTemplate.opsForZSet().size(rankKey.generate());
        assertThat(size).isEqualTo(5);

        assertThat(products.getProducts()).hasSize(5)
            .extracting("productId")
            .containsExactly(
                product1.getId(),
                product5.getId(),
                product2.getId(),
                product3.getId(),
                product4.getId()
            );
    }
}