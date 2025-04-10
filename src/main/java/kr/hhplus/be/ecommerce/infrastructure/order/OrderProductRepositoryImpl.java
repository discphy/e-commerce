package kr.hhplus.be.ecommerce.infrastructure.order;

import kr.hhplus.be.ecommerce.domain.order.OrderProduct;
import kr.hhplus.be.ecommerce.domain.order.OrderProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderProductRepositoryImpl implements OrderProductRepository {

    @Override
    public List<OrderProduct> findOrderIdsIn(List<Long> orderIds) {
        return List.of();
    }
}
