package kr.hhplus.be.ecommerce.application.product;

import kr.hhplus.be.ecommerce.MockTestSupport;
import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.domain.payment.PaymentInfo;
import kr.hhplus.be.ecommerce.domain.payment.PaymentService;
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

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

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
        PaymentInfo.Orders orders = PaymentInfo.Orders.of(List.of(1L, 2L, 3L));
        when(paymentService.getCompletedOrdersBetweenDays(3))
            .thenReturn(orders);

        OrderInfo.TopPaidProducts topPaidProducts = OrderInfo.TopPaidProducts.of(List.of(6L, 5L, 4L));
        when(orderService.getTopPaidProducts(any()))
            .thenReturn(topPaidProducts);

        ProductInfo.Products products = ProductInfo.Products.of(List.of(
            ProductInfo.Product.builder()
                .productId(6L)
                .productName("상품명1")
                .productPrice(1_000L)
                .build(),
            ProductInfo.Product.builder()
                .productId(5L)
                .productName("상품명2")
                .productPrice(2_000L)
                .build(),
            ProductInfo.Product.builder()
                .productId(4L)
                .productName("상품명3")
                .productPrice(3_000L)
                .build()
        ));

        when(productService.getProducts(any()))
            .thenReturn(products);

        when(stockService.getStock(anyLong()))
            .thenReturn(StockInfo.Stock.of(1L, 10));

        // when
        ProductResult.Products popularProducts = productFacade.getPopularProducts();

        // then
        InOrder inOrder = inOrder(paymentService, orderService, productService);
        inOrder.verify(paymentService, times(1)).getCompletedOrdersBetweenDays(3);
        inOrder.verify(orderService, times(1)).getTopPaidProducts(any());
        inOrder.verify(productService, times(1)).getProducts(any());

        assertThat(popularProducts.getProducts()).hasSize(3)
            .extracting("productId", "productName", "productPrice")
            .containsExactly(
                tuple(6L, "상품명1", 1_000L),
                tuple(5L, "상품명2", 2_000L),
                tuple(4L, "상품명3", 3_000L)
            );
    }
}