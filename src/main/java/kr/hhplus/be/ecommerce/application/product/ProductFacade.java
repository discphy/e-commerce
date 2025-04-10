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

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private static final int RECENT_DAYS = 3;
    private static final int TOP_LIMIT = 5;

    private final ProductService productService;
    private final StockService stockService;
    private final PaymentService paymentService;
    private final OrderProductService orderProductService;

    public ProductResult.Products getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return ProductResult.Products.of(products.getProducts().stream()
            .map(this::getProduct)
            .toList());
    }

    public ProductResult.Products getPopularProducts() {
        PaymentInfo.Orders completedOrders = paymentService.getCompletedOrdersBetweenDays(RECENT_DAYS);

        OrderProductCommand.TopOrders orderProductCommand = OrderProductCommand.TopOrders.of(completedOrders.getOrderIds(), TOP_LIMIT);
        OrderProductInfo.TopPaidProducts topPaidProducts = orderProductService.getTopPaidProducts(orderProductCommand);
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
