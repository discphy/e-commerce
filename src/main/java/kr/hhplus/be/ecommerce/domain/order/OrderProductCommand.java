package kr.hhplus.be.ecommerce.domain.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderProductCommand {

    @Getter
    public static class TopOrders {

        private final List<Long> orderIds;
        private final int limit;

        private TopOrders(List<Long> orderIds, int limit) {
            this.orderIds = orderIds;
            this.limit = limit;
        }

        public static TopOrders of(List<Long> orderIds, int limit) {
            return new TopOrders(orderIds, limit);
        }
    }
}
