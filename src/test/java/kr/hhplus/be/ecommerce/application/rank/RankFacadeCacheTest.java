package kr.hhplus.be.ecommerce.application.rank;

import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import kr.hhplus.be.ecommerce.domain.rank.Rank;
import kr.hhplus.be.ecommerce.domain.rank.RankRepository;
import kr.hhplus.be.ecommerce.infrastructure.cache.RedisCacheTemplate;
import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import kr.hhplus.be.ecommerce.support.cache.CacheType;
import kr.hhplus.be.ecommerce.test.support.database.RedisCacheCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class RankFacadeCacheTest extends IntegrationTestSupport {

    @Autowired
    private RankFacade rankFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private RedisCacheCleaner redisCacheCleaner;
    
    private final String cacheKey = "top:5:days:3";

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

        List<Rank> ranks = List.of(
            Rank.createSell(product1.getId(), LocalDate.now().minusDays(1), 10L),
            Rank.createSell(product2.getId(), LocalDate.now().minusDays(1), 34L),
            Rank.createSell(product3.getId(), LocalDate.now().minusDays(2), 42L)
        );

        ranks.forEach(rankRepository::save);
    }

    @AfterEach
    void tearDown() {
        redisCacheCleaner.clean();
    }

    @DisplayName("인기 상품을 캐싱 조회 한다.")
    @Test
    void getPopularProducts() {
        // given
        Optional<RankResult.PopularProducts> emptyCached = redisCacheTemplate.get(CacheType.POPULAR_PRODUCT, cacheKey, RankResult.PopularProducts.class);

        // when
        rankFacade.getPopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        // then
        assertThat(emptyCached).isEmpty();

        RankResult.PopularProducts cached = redisCacheTemplate.get(CacheType.POPULAR_PRODUCT, cacheKey, RankResult.PopularProducts.class).orElseThrow();
        assertThat(cached.getProducts()).hasSize(3)
            .extracting("productId")
            .containsExactly(product3.getId(), product2.getId(), product1.getId());
    }

    @DisplayName("인기 상품을 캐싱 한다.")
    @Test
    void updatePopularProductsForCache() {
        // given
        Optional<RankResult.PopularProducts> emptyCached = redisCacheTemplate.get(CacheType.POPULAR_PRODUCT, cacheKey, RankResult.PopularProducts.class);

        // when
        rankFacade.updatePopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        // then
        assertThat(emptyCached).isEmpty();

        RankResult.PopularProducts cached = redisCacheTemplate.get(CacheType.POPULAR_PRODUCT, cacheKey, RankResult.PopularProducts.class).orElseThrow();
        assertThat(cached.getProducts()).hasSize(3)
            .extracting("productId")
            .containsExactly(product3.getId(), product2.getId(), product1.getId());
    }

    @DisplayName("인기 상품을 캐시 갱신 한다.")
    @Test
    void updatePopularProductsForRefresh() {
        // given
        redisCacheTemplate.put(CacheType.POPULAR_PRODUCT, cacheKey, "test");
        Optional<String> existCached = redisCacheTemplate.get(CacheType.POPULAR_PRODUCT, cacheKey, String.class);

        // when
        rankFacade.updatePopularProducts(RankCriteria.PopularProducts.ofTop5Days3());

        // then
        assertThat(existCached).isPresent();
        assertThat(existCached.get()).isEqualTo("test");

        RankResult.PopularProducts cached = redisCacheTemplate.get(CacheType.POPULAR_PRODUCT, cacheKey, RankResult.PopularProducts.class).orElseThrow();
        assertThat(cached.getProducts()).hasSize(3)
            .extracting("productId")
            .containsExactly(product3.getId(), product2.getId(), product1.getId());
    }
}