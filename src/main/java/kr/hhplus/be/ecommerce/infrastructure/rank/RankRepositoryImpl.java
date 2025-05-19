package kr.hhplus.be.ecommerce.infrastructure.rank;

import kr.hhplus.be.ecommerce.domain.rank.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RankRepositoryImpl implements RankRepository {

    private final RankJpaRepository rankJpaRepository;
    private final RankRedisRepository rankRedisRepository;
    private final RankJdbcTemplateRepository rankJdbcTemplateRepository;

    @Override
    public Rank save(Rank rank) {
        return rankRedisRepository.save(rank);
    }

    @Override
    public List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.Query command) {
        return rankRedisRepository.findPopularSellRanks(command);
    }

    @Override
    public List<RankInfo.PopularProduct> findDailyRank(RankKey key) {
        return rankRedisRepository.findDailyRank(key);
    }

    @Override
    public List<Rank> findBy(RankType rankType, LocalDate date) {
        return rankJpaRepository.findByRankTypeAndRankDate(rankType, date);
    }

    @Override
    public void saveAll(List<Rank> ranks) {
        rankJdbcTemplateRepository.batchInsert(ranks);
    }

    @Override
    public boolean delete(RankKey key) {
        return rankRedisRepository.delete(key);
    }
}
