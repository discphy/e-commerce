package kr.hhplus.be.ecommerce.application.product;

import kr.hhplus.be.ecommerce.domain.order.*;
import kr.hhplus.be.ecommerce.domain.payment.PaymentInfo;
import kr.hhplus.be.ecommerce.domain.payment.PaymentService;
import kr.hhplus.be.ecommerce.domain.product.ProductCommand;
import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.domain.stock.StockInfo;
import kr.hhplus.be.ecommerce.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private static final int RECENT_DAYS = 3;
    private static final int TOP_LIMIT = 5;

    private final ProductService productService;
    private final StockService stockService;
    private final PaymentService paymentService;
    private final OrderService orderService;

    @Transactional(readOnly = true)
    public ProductResult.Products getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return ProductResult.Products.of(products.getProducts().stream()
            .map(this::getProduct)
            .toList());
    }

    @Transactional(readOnly = true)
    public ProductResult.Products getPopularProducts() {
        PaymentInfo.Orders completedOrders = paymentService.getCompletedOrdersBetweenDays(RECENT_DAYS);

        OrderCommand.TopOrders orderProductCommand = OrderCommand.TopOrders.of(completedOrders.getOrderIds(), TOP_LIMIT);
        OrderInfo.TopPaidProducts topPaidProducts = orderService.getTopPaidProducts(orderProductCommand);
        ProductInfo.Products products = productService.getProducts(ProductCommand.Products.of(topPaidProducts.getProductIds()));

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
