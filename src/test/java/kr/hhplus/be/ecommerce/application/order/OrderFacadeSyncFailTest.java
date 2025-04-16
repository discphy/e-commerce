package kr.hhplus.be.ecommerce.application.order;

import kr.hhplus.be.ecommerce.IntegrationTestSupport;
import kr.hhplus.be.ecommerce.domain.balance.Balance;
import kr.hhplus.be.ecommerce.domain.balance.BalanceRepository;
import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.product.ProductRepository;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import kr.hhplus.be.ecommerce.domain.stock.Stock;
import kr.hhplus.be.ecommerce.domain.stock.StockRepository;
import kr.hhplus.be.ecommerce.domain.user.User;
import kr.hhplus.be.ecommerce.domain.user.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
class OrderFacadeSyncFailTest extends IntegrationTestSupport {

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

    @DisplayName("동시에 주문 결제 요청 시, 데드락 예외가 터지는 동시성 문제 발생")
    @Test
    void verifyDeadlockDuringConcurrentOrderPayment() throws InterruptedException {
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

        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<OrderResult.Order>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            Future<OrderResult.Order> future = executorService.submit(() -> {
                try {
                    return orderFacade.orderPayment(criteria);
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await();
        executorService.shutdown();

        // then
        List<Throwable> errors = new ArrayList<>();
        for (Future<OrderResult.Order> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                errors.add(e.getCause());
            }
        }

        // 데드락 발생 검증
        // 트랜잭션 A: Balance 행 잠금 획득 -> Stock 행 잠금 시도(대기)
        // 트랜잭션 B: Stock 행 잠금 획득 -> Balance 행 잠금 시도(대기)
        assertThat(errors).anyMatch(CannotAcquireLockException.class::isInstance);

        // 데드락으로 인한 3건 중 1건의 주문만 정상 처리됨
        Balance remainBalance = balanceRepository.findOptionalByUserId(user.getId()).orElseThrow();
        Stock remainStock = stockRepository.findByProductId(product.getId());

        assertThat(remainBalance.getAmount()).isEqualTo(400_000L);
        assertThat(remainStock.getQuantity()).isEqualTo(99);
    }
}
