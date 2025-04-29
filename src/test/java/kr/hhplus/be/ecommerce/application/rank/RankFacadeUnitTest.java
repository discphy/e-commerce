package kr.hhplus.be.ecommerce.application.rank;

import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import kr.hhplus.be.ecommerce.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RankFacadeUnitTest extends MockTestSupport {

    @InjectMocks
    private RankFacade rankFacade;

    @Mock
    private OrderService orderService;

    @Mock
    private RankService rankService;

    @DisplayName("일별 랭킹을 생성한다.")
    @Test
    void createDailyRankAt() {
        // given
        OrderInfo.PaidProducts paidProducts = OrderInfo.PaidProducts.of(
            List.of(
                OrderInfo.PaidProduct.of(1L, 10),
                OrderInfo.PaidProduct.of(2L, 20)
            )
        );

        when(orderService.getPaidProducts(any()))
            .thenReturn(paidProducts);

        LocalDate yesterday = LocalDate.now().minusDays(1);

        // when
        rankFacade.createDailyRankAt(yesterday);

        // then
        InOrder inOrder = inOrder(orderService, rankService);
        inOrder.verify(orderService, times(1)).getPaidProducts(any());
        inOrder.verify(rankService, times(1)).createSellRank(any());
    }

}