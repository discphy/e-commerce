package kr.hhplus.be.ecommerce.domain.user;

import kr.hhplus.be.ecommerce.test.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("사용자가 반드시 존재해야 한다.")
    @Test
    void findByIdShouldExists() {
        // given
        Long userId = 1L;

        // when & then
        assertThatThrownBy(() -> userRepository.findById(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("사용자를 가져온다.")
    @Test
    void findById() {
        // given
        User user = User.create("항플");
        userRepository.save(user);

        // when
        User result = userRepository.findById(user.getId());

        // then
        assertThat(result).isEqualTo(user);
        assertThat(result.getId()).isNotNull();
    }

}