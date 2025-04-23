package kr.hhplus.be.ecommerce.application.product;

import kr.hhplus.be.ecommerce.domain.product.ProductCommand;
import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.domain.rank.RankCommand;
import kr.hhplus.be.ecommerce.domain.rank.RankInfo;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import kr.hhplus.be.ecommerce.domain.stock.StockInfo;
import kr.hhplus.be.ecommerce.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private static final int RECENT_DAYS = 3;
    private static final int TOP_LIMIT = 5;

    private final ProductService productService;
    private final StockService stockService;
    private final RankService rankService;

    @Transactional(readOnly = true)
    public ProductResult.Products getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return ProductResult.Products.of(products.getProducts().stream()
            .map(this::getProduct)
            .toList());
    }

    @Transactional(readOnly = true)
    public ProductResult.Products getPopularProducts() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(RECENT_DAYS);

        RankCommand.PopularSellRank popularSellRankCommand = RankCommand.PopularSellRank.of(TOP_LIMIT, startDate, endDate);
        RankInfo.PopularProducts popularProducts = rankService.getPopularSellRank(popularSellRankCommand);

        ProductCommand.Products productsCommand = ProductCommand.Products.of(popularProducts.getProductIds());
        ProductInfo.Products products = productService.getProducts(productsCommand);

        return ProductResult.Products.of(products.getProducts().stream()
            .map(this::getProduct)
            .toList());
    }

    private ProductResult.Product getProduct(ProductInfo.Product product) {
        StockInfo.Stock stock = stockService.getStock(product.getProductId());

        return ProductResult.Product.builder()
            .productId(product.getProductId())
            .productName(product.getProductName())
            .productPrice(product.getProductPrice())
            .quantity(stock.getQuantity())
            .build();
    }

}
