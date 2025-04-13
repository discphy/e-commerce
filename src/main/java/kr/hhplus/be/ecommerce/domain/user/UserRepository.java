package kr.hhplus.be.ecommerce.domain.user;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {

    User findById(Long userId);
}
