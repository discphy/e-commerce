package kr.hhplus.be.ecommerce.infrastructure.rank;

import kr.hhplus.be.ecommerce.domain.rank.Rank;
import kr.hhplus.be.ecommerce.domain.rank.RankCommand;
import kr.hhplus.be.ecommerce.domain.rank.RankInfo;
import kr.hhplus.be.ecommerce.domain.rank.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RankRepositoryImpl implements RankRepository {

    private final RankQueryDslRepository rankQueryDslRepository;
    private final RankJpaRepository rankJpaRepository;
    private final RankRedisRepository rankRedisRepository;

    @Override
    public Rank save(Rank rank) {
        return rankRedisRepository.save(rank);
    }

    @Override
    public List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.Query command) {
        return rankRedisRepository.findPopularSellRanks(command);
    }
}
