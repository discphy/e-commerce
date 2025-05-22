package kr.hhplus.be.ecommerce.interfaces.rank.scheduler;

import kr.hhplus.be.ecommerce.domain.rank.RankCommand;
import kr.hhplus.be.ecommerce.domain.rank.RankConstant;
import kr.hhplus.be.ecommerce.domain.rank.RankService;
import kr.hhplus.be.ecommerce.support.cache.CacheType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankScheduler {

    private final RankService rankService;

    @Scheduled(cron = "0 */5 * * * *")
    @CachePut(value = CacheType.CacheName.POPULAR_PRODUCT, key = "'top:5:days:3'")
    public void putPopularProducts() {
        log.info("실시간 인기상품 캐싱 스케줄러 실행");
        try {
            rankService.getPopularProducts(RankCommand.PopularProducts.ofTop5Days3(LocalDate.now()));
            log.info("실시간 인기상품 캐싱 스케줄러 완료");
        } catch (Exception e) {
            log.error("실시간 인기상품 캐싱 스케줄러 실행 중 오류 발생", e);
        }
    }

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
