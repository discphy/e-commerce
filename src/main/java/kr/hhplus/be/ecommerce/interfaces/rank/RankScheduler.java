package kr.hhplus.be.ecommerce.interfaces.rank;

import kr.hhplus.be.ecommerce.application.rank.RankFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankScheduler {

    private final RankFacade rankFacade;

    @Scheduled(cron = "0 0 1 * * *")
    public void createDailyRank() {
        log.info("일별 판매 랭크 생성 스케줄러 실행");
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            rankFacade.createDailyRankAt(yesterday);
            log.info("일별 판매 랭크 생성 완료: {}", yesterday);
        } catch (Exception e) {
            log.error("일별 판매 랭크 생성 스케줄러 실행 중 오류 발생", e);
        }
    }
}
