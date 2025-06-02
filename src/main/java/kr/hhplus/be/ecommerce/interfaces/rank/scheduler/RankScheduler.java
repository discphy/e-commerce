package kr.hhplus.be.ecommerce.interfaces.rank.scheduler;

import kr.hhplus.be.ecommerce.domain.rank.RankConstant;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankScheduler {

    private final RankService rankService;

    @Scheduled(cron = "10 0 0 * * *")
    public void persistDailyRank() {
        log.info("일일 판매량 DB 영속 스케줄러 실행");
        try {
            rankService.persistDailyRank(LocalDate.now().minusDays(RankConstant.PERSIST_DAYS));
            log.info("일일 판매량 DB 영속 스케줄러 완료");
        } catch (Exception e) {
            log.error("일일 판매량 DB 영속 스케줄러 실행 중 오류 발생", e);
        }
    }
}
