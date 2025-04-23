package kr.hhplus.be.ecommerce.application.product;

import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.domain.rank.RankInfo;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import kr.hhplus.be.ecommerce.domain.stock.StockInfo;
import kr.hhplus.be.ecommerce.domain.stock.StockService;
import kr.hhplus.be.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductFacadeUnitTest extends MockTestSupport {

    @InjectMocks
    private ProductFacade productFacade;

    @Mock
    private ProductService productService;

    @Mock
    private StockService stockService;

    @Mock
    private RankService rankService;

    @DisplayName("판매 가능 상품 목록을 조회한다.")
    @Test
    void getProducts() {
        // given
        ProductInfo.Products products = mock(ProductInfo.Products.class);

        when(products.getProducts())
            .thenReturn(
                List.of(
                    ProductInfo.Product.builder()
                        .productId(1L)
                        .productName("상품명1")
                        .productPrice(1_000L)
                        .build(),
                    ProductInfo.Product.builder()
                        .productId(2L)
                        .productName("상품명2")
                        .productPrice(2_000L)
                        .build()
                )
            );

        when(productService.getSellingProducts())
            .thenReturn(products);

        when(stockService.getStock(anyLong()))
            .thenReturn(StockInfo.Stock.of(1L, 10));

        // when
        ProductResult.Products result = productFacade.getProducts();

        // then
        InOrder inOrder = inOrder(productService, stockService);
        inOrder.verify(productService, times(1)).getSellingProducts();
        inOrder.verify(stockService, times(2)).getStock(anyLong());

        assertThat(result.getProducts()).hasSize(2)
            .extracting("productId", "quantity")
            .containsExactlyInAnyOrder(
                tuple(1L, 10),
                tuple(2L, 10)
            );
    }

    @DisplayName("최근 3일 가장 많이 팔린 상위 상품 5개를 조회한다.")
    @Test
    void getPopularProducts() {
        // given
        RankInfo.PopularProducts rankPopularProducts = RankInfo.PopularProducts.of(List.of(
            RankInfo.PopularProduct.of(1L, 120L),  // 1등 상품
            RankInfo.PopularProduct.of(2L, 95L),   // 2등 상품
            RankInfo.PopularProduct.of(3L, 87L),   // 3등 상품
            RankInfo.PopularProduct.of(4L, 76L),   // 4등 상품
            RankInfo.PopularProduct.of(5L, 65L)   // 5등 상품
        ));
        when(rankService.getPopularSellRank(any()))
            .thenReturn(rankPopularProducts);

        ProductInfo.Products products = ProductInfo.Products.of(List.of(
            ProductInfo.Product.builder()
                .productId(1L)
                .productName("상품명1")
                .productPrice(1_000L)
                .build(),
            ProductInfo.Product.builder()
                .productId(2L)
                .productName("상품명2")
                .productPrice(2_000L)
                .build(),
            ProductInfo.Product.builder()
                .productId(3L)
                .productName("상품명3")
                .productPrice(3_000L)
                .build(),
            ProductInfo.Product.builder()
                .productId(4L)
                .productName("상품명4")
                .productPrice(4_000L)
                .build(),
            ProductInfo.Product.builder()
                .productId(5L)
                .productName("상품명5")
                .productPrice(5_000L)
                .build()
        ));

        when(productService.getProducts(any()))
            .thenReturn(products);

        when(stockService.getStock(anyLong()))
            .thenReturn(StockInfo.Stock.of(1L, 10))
            .thenReturn(StockInfo.Stock.of(2L, 10))
            .thenReturn(StockInfo.Stock.of(3L, 10))
            .thenReturn(StockInfo.Stock.of(4L, 10))
            .thenReturn(StockInfo.Stock.of(5L, 10));


        // when
        ProductResult.Products popularProducts = productFacade.getPopularProducts();

        // then
        InOrder inOrder = inOrder(rankService, productService, stockService);
        inOrder.verify(rankService, times(1)).getPopularSellRank(any());
        inOrder.verify(productService, times(1)).getProducts(any());
        inOrder.verify(stockService, times(5)).getStock(anyLong());

        assertThat(popularProducts.getProducts()).hasSize(5)
            .extracting("productId", "productName", "productPrice")
            .containsExactly(
                tuple(1L, "상품명1", 1_000L),
                tuple(2L, "상품명2", 2_000L),
                tuple(3L, "상품명3", 3_000L),
                tuple(4L, "상품명4", 4_000L),
                tuple(5L, "상품명5", 5_000L)
            );
    }
}