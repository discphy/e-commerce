package kr.hhplus.be.ecommerce.infrastructure.user;

import kr.hhplus.be.ecommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
