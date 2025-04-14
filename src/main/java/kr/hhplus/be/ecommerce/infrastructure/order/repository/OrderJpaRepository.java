package kr.hhplus.be.ecommerce.infrastructure.order.repository;

import kr.hhplus.be.ecommerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
