package kr.hhplus.be.ecommerce.infrastructure.rank;

import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.domain.rank.*;
import kr.hhplus.be.ecommerce.infrastructure.product.ProductJpaRepository;
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
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Rank save(Rank rank) {
        return rankRedisRepository.save(rank);
    }

    @Override
    public List<RankInfo.ProductScore> findProductScores(RankCommand.Query command) {
        return rankRedisRepository.findPopularSellRanks(command);
    }

    @Override
    public List<RankInfo.ProductScore> findDailyRank(RankKey key) {
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

    @Override
    public Product findProductById(Long productId) {
        return productJpaRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
    }
}
