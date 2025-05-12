package kr.hhplus.be.ecommerce.application.rank;

import kr.hhplus.be.ecommerce.domain.order.OrderCommand;
import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankFacade {

    private final ProductService productService;
    private final OrderService orderService;
    private final RankService rankService;

    @Transactional
    public void createDailyRankAt(LocalDate date) {
        OrderCommand.DateQuery orderCommand = OrderCommand.DateQuery.of(date);
        OrderInfo.PaidProducts paidProducts = orderService.getPaidProducts(orderCommand);

        RankCommand.CreateList rankCommand = createListCommand(paidProducts, date);
        rankService.createSellRank(rankCommand);
    }

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

    private RankCommand.CreateList createListCommand(OrderInfo.PaidProducts paidProducts, LocalDate yesterday) {
        List<RankCommand.Create> commands = paidProducts.getProducts().stream()
            .map(product -> createCommand(product, yesterday))
            .toList();

        return RankCommand.CreateList.of(commands);
    }

    private RankCommand.Create createCommand(OrderInfo.PaidProduct product, LocalDate yesterday) {
        return RankCommand.Create.of(
            product.getProductId(),
            product.getQuantity(),
            yesterday
        );
    }

    private RankResult.PopularProducts getPopularProducts(int top, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        RankCommand.PopularSellRank popularSellRankCommand = RankCommand.PopularSellRank.of(top, startDate, endDate);
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
