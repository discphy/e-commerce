package kr.hhplus.be.ecommerce.order.filter;

import kr.hhplus.be.ecommerce.order.support.ContainerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(LoggingFilterIntegrationTest.DummyController.class)
class LoggingFilterIntegrationTest extends ContainerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @DisplayName("로깅 필터가 정상적으로 동작한다.")
    @Test
    void doFilterInternal() throws Exception {
        // when
        MvcResult result = mockMvc.perform(
            post("/api/test")
                .content("sample")
                .contentType(MediaType.TEXT_PLAIN)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        // then
        String traceId = result.getResponse().getHeader("X-Trace-Id");
        assertThat(traceId).isNotBlank();
    }

    @DisplayName("예외가 발생해도 로깅 필터가 정상적으로 동작한다.")
    @Test
    void doFilterInternalWithException() throws Exception {
        // when
        MvcResult result = mockMvc.perform(
                post("/api/test/exception")
                    .content("sample")
                    .contentType(MediaType.TEXT_PLAIN)
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn();

        // then
        String traceId = result.getResponse().getHeader("X-Trace-Id");
        assertThat(traceId).isNotBlank();
    }

    @RestController
    static class DummyController {
        @PostMapping("/api/test")
        public ResponseEntity<String> test(@RequestBody String body) {
            return ResponseEntity.ok("response-body");
        }

        @PostMapping("/api/test/exception")
        public ResponseEntity<String> testException(@RequestBody String body) {
            throw new IllegalArgumentException("test exception");
        }
    }
}