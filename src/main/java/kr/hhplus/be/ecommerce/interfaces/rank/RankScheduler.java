package kr.hhplus.be.ecommerce.interfaces.rank;

import kr.hhplus.be.ecommerce.application.rank.RankCriteria;
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

    @Scheduled(cron = "0 */5 * * * *")
    public void createDailyRank() {
        log.info("실시간 인기상품 캐싱 스케줄러 실행");
        try {
            rankFacade.updatePopularProducts(RankCriteria.PopularProducts.ofTop5Days3());
            log.info("실시간 인기상품 캐싱 스케줄러 완료");
        } catch (Exception e) {
            log.error("실시간 인기상품 캐싱 스케줄러 실행 중 오류 발생", e);
        }
    }
}
