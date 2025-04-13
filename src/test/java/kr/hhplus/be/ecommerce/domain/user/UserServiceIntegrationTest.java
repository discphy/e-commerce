package kr.hhplus.be.ecommerce.domain.user;

import kr.hhplus.be.ecommerce.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class UserServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("사용자 조회 시, 사용자 정보가 존재해야 한다.")
    @Test
    void getUserWithEmptyUser() {
        // given
        Long userId = 1L;

        // when & then
        assertThatThrownBy(() -> userService.getUser(userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("사용자를 조회한다.")
    @Test
    void getUser() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        // when
        UserInfo.User result = userService.getUser(user.getId());

        // then
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
    }

}