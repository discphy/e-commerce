package kr.hhplus.be.ecommerce.application.order;

import kr.hhplus.be.ecommerce.support.ConcurrencyTestSupport;
import kr.hhplus.be.ecommerce.domain.balance.Balance;
import kr.hhplus.be.ecommerce.domain.balance.BalanceRepository;
import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import kr.hhplus.be.ecommerce.domain.stock.Stock;
import kr.hhplus.be.ecommerce.domain.stock.StockRepository;
import kr.hhplus.be.ecommerce.domain.user.User;
import kr.hhplus.be.ecommerce.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderFacadeConcurrencyTest extends ConcurrencyTestSupport {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @DisplayName("동시성 - 모든 주문이 정상 처리 되어야 한다.")
    @Test
    void orderPaymentConcurrency() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        Balance balance = Balance.create(user.getId(), 500_000L);
        balanceRepository.save(balance);

        Product product = Product.create("항플 블랙 뱃지", 100_000L, ProductSellingStatus.SELLING);
        productRepository.save(product);

        Stock stock = Stock.create(product.getId(), 100);
        stockRepository.save(stock);

        OrderCriteria.OrderPayment criteria = OrderCriteria.OrderPayment.of(user.getId(), null,
            List.of(OrderCriteria.OrderProduct.of(product.getId(), 1))
        );

        // when
        executeConcurrency(3, () -> orderFacade.orderPayment(criteria));

        // then
        Balance remainBalance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        Stock remainStock = stockRepository.findByProductId(product.getId());

        assertThat(remainBalance.getAmount()).isEqualTo(200_000L);
        assertThat(remainStock.getQuantity()).isEqualTo(97);
    }
}
