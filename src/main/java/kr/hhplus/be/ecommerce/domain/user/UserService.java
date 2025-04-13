package kr.hhplus.be.ecommerce.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfo.User getUser(Long userId) {
        User user = userRepository.findById(userId);

        return UserInfo.User.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .build();
    }
}
