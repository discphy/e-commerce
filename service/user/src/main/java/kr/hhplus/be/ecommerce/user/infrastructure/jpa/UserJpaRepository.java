package kr.hhplus.be.ecommerce.user.infrastructure.jpa;

import kr.hhplus.be.ecommerce.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
