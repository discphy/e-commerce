package kr.hhplus.be.ecommerce.domain.rank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankEventListener {

    private final RankService rankService;

    @Async
    @EventListener
    public void handle(RankEvent.Created event) {
        log.info("랭크 생성 이벤트 수신: {}", event.getRanks());
        rankService.updatedPopularProducts(RankCommand.PopularProducts.ofTop5Days3(LocalDate.now()));
    }
}
