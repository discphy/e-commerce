package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderProductServiceTest extends MockTestSupport {

    @InjectMocks
    private OrderProductService orderProductService;

    @Mock
    private OrderProductRepository orderProductRepository;

    @DisplayName("가장 많이 주문 결제 완료된 상품 목록을 가져온다.")
    @Test
    void getTopPaidProducts() {
        // given
        OrderProductCommand.TopOrders command = mock(OrderProductCommand.TopOrders.class);

        List<OrderProduct> orderProducts = List.of(
            OrderProduct.builder()
                .productId(1L)
                .quantity(2)
                .build(),
            OrderProduct.builder()
                .productId(2L)
                .quantity(3)
                .build(),
            OrderProduct.builder()
                .productId(3L)
                .quantity(1)
                .build(),
            OrderProduct.builder()
                .productId(4L)
                .quantity(4)
                .build(),
            OrderProduct.builder()
                .productId(5L)
                .quantity(5)
                .build(),
            OrderProduct.builder()
                .productId(1L)
                .quantity(5)
                .build()
        );

        when(orderProductRepository.findOrderIdsIn(anyList()))
            .thenReturn(orderProducts);

        // when
        OrderProductInfo.TopPaidProducts topPaidProducts = orderProductService.getTopPaidProducts(command);

        // then
        assertThat(topPaidProducts.getProductIds()).hasSize(5)
            .containsExactly(1L, 5L, 4L, 2L, 3L);

    }

}