package kr.hhplus.be.ecommerce.product.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import kr.hhplus.be.ecommerce.product.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingFilterUnitTest extends MockTestSupport {

    @InjectMocks
    private LoggingFilter loggingFilter;

    @Mock
    private FilterChain filterChain;

    @DisplayName("로깅 필터가 정상적으로 동작한다.")
    @Test
    void doFilterInternal() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // when
        loggingFilter.doFilterInternal(requestWrapper, responseWrapper, filterChain);

        // then
        String traceId = response.getHeader("X-Trace-Id");
        assertThat(traceId).isNotNull();
    }
}