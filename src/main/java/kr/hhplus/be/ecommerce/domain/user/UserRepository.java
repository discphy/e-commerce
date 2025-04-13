package kr.hhplus.be.ecommerce.domain.user;

import org.springframework.stereotype.Component;

@Component
public interface UserRepository {

    User save(User user);

    User findById(Long userId);
}
