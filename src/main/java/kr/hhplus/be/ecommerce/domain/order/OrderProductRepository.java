package kr.hhplus.be.ecommerce.domain.order;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository {

    List<OrderProduct> findOrderIdsIn(List<Long> orderIds);
}
