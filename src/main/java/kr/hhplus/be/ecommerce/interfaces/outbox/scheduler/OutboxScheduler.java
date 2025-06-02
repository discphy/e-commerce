package kr.hhplus.be.ecommerce.interfaces.outbox.scheduler;

import kr.hhplus.be.ecommerce.domain.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxService outboxService;

    @Scheduled(fixedDelay = 10, initialDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void publishPendingEvent() {
        log.info("Outbox 스케줄러 실행");
        try {
            outboxService.publishPendingEvent();
            log.info("Outbox 스케줄러 완료");
        } catch (Exception e) {
            log.error("Outbox 스케줄러 실행 중 오류 발생", e);
        }
    }

}
