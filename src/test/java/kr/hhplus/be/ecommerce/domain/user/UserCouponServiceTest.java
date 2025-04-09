package kr.hhplus.be.ecommerce.domain.user;

import kr.hhplus.be.ecommerce.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UserCouponServiceTest extends MockTestSupport {

    @InjectMocks
    private UserCouponService userCouponService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @DisplayName("사용자 쿠폰을 생성한다.")
    @Test
    void createUserCoupon() {
        // given
        UserCouponCommand.Publish command = UserCouponCommand.Publish.of(1L, 1L);

        // when
        userCouponService.createUserCoupon(command);

        // then
        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
    }

}