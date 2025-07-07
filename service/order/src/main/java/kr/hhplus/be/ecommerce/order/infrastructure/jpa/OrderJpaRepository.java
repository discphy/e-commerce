package kr.hhplus.be.ecommerce.order.infrastructure.jpa;

import kr.hhplus.be.ecommerce.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
