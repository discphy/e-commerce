package kr.hhplus.be.ecommerce.application.rank;

import kr.hhplus.be.ecommerce.domain.product.ProductCommand;
import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.domain.rank.RankCommand;
import kr.hhplus.be.ecommerce.domain.rank.RankInfo;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import kr.hhplus.be.ecommerce.support.cache.CacheType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final ProductService productService;
    private final RankService rankService;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheType.CacheName.POPULAR_PRODUCT, key = "'top:' + #criteria.top + ':days:' + #criteria.days")
    public RankResult.PopularProducts getPopularProducts(RankCriteria.PopularProducts criteria) {
        return getPopularProducts(criteria.getTop(), criteria.getDays());
    }

    @Transactional(readOnly = true)
    @CachePut(value = CacheType.CacheName.POPULAR_PRODUCT, key = "'top:' + #criteria.top + ':days:' + #criteria.days")
    public RankResult.PopularProducts updatePopularProducts(RankCriteria.PopularProducts criteria) {
        return getPopularProducts(criteria.getTop(), criteria.getDays());
    }

    @Transactional
    public void persistDailyRank(RankCriteria.PersistDailyRank criteria) {
        rankService.persistDailyRank(criteria.getDate());
    }

    private RankResult.PopularProducts getPopularProducts(int top, int days) {
        LocalDate now = LocalDate.now();

        RankCommand.PopularSellRank popularSellRankCommand = RankCommand.PopularSellRank.of(top, days, now);
        RankInfo.PopularProducts popularProducts = rankService.getPopularSellRank(popularSellRankCommand);

        ProductCommand.Products productsCommand = ProductCommand.Products.of(popularProducts.getProductIds());
        ProductInfo.Products products = productService.getProducts(productsCommand);

        return RankResult.PopularProducts.of(products.getProducts().stream()
            .map(this::toPopularProduct)
            .toList());
    }

    private RankResult.PopularProduct toPopularProduct(ProductInfo.Product product) {
        return RankResult.PopularProduct.builder()
            .productId(product.getProductId())
            .productName(product.getProductName())
            .productPrice(product.getProductPrice())
            .build();
    }
}
