package kr.hhplus.be.ecommerce.application.product;

import kr.hhplus.be.ecommerce.MockTestSupport;
import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.domain.stock.StockInfo;
import kr.hhplus.be.ecommerce.domain.stock.StockService;
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

class ProductFacadeTest extends MockTestSupport {

    @InjectMocks
    private ProductFacade productFacade;

    @Mock
    private ProductService productService;

    @Mock
    private StockService stockService;

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
}