package kr.hhplus.be.ecommerce.domain.rank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;

    public List<Rank> createSellRank(RankCommand.CreateList command) {
        return command.getRanks().stream()
            .map(this::createSell)
            .map(rankRepository::save)
            .toList();
    }

    public RankInfo.PopularProducts getPopularSellRank(RankCommand.PopularSellRank command) {
        RankKey target = RankKey.ofDays(RankType.SELL, command.getDays());
        RankKeys sources = RankKeys.ofDaysWithDate(RankType.SELL, command.getDays(), command.getDate());

        RankCommand.Query query = RankCommand.Query.of(command.getTop(), target, sources);
        List<RankInfo.PopularProduct> popularProducts = rankRepository.findPopularSellRanks(query);

        return RankInfo.PopularProducts.of(popularProducts);
    }

    public void persistDailyRank(LocalDate date) {
        RankKey key = RankKey.ofDate(RankType.SELL, date);
        List<RankInfo.PopularProduct> popularProducts = rankRepository.findDailyRank(key);

        if (popularProducts.isEmpty()) {
            log.info("일일 판매 링크가 존재하지 않습니다. date: {}", date);
            return;
        }

        List<Rank> ranks = popularProducts.stream()
            .map(ps -> Rank.createSell(ps.getProductId(), date, ps.getTotalScore()))
            .toList();

        rankRepository.saveAll(ranks);
        rankRepository.delete(key);
    }

    private Rank createSell(RankCommand.Create command) {
        return Rank.createSell(command.getProductId(), command.getRankDate(), command.getScore());
    }
}
