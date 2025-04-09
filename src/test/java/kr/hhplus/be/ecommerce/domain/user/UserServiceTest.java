package kr.hhplus.be.ecommerce.domain.user;

import kr.hhplus.be.ecommerce.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class UserServiceTest extends MockTestSupport {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("유효한 ID로 사용자를 조회해야 한다.")
    @Test
    void getUserWithInvalidId() {
        // given
        when(userRepository.findById(anyLong()))
            .thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> userService.getUser(anyLong()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("사용자를 조회한다.")
    @Test
    void getUser() {
        // given
        when(userRepository.findById(anyLong()))
            .thenReturn(User.of("항플"));

        // when
        UserInfo.User user = userService.getUser(anyLong());

        // then
        assertThat(user.getUsername()).isEqualTo("항플");
    }

}