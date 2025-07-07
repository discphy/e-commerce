package kr.hhplus.be.ecommerce.user.domain;

import org.springframework.stereotype.Component;

@Component
public interface UserRepository {

    User save(User user);

    User findById(Long userId);
}
