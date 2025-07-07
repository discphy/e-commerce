package kr.hhplus.be.ecommerce.product.infrastructure;

import kr.hhplus.be.ecommerce.product.domain.product.Product;
import kr.hhplus.be.ecommerce.product.domain.rank.*;
import kr.hhplus.be.ecommerce.product.infrastructure.jpa.ProductJpaRepository;
import kr.hhplus.be.ecommerce.product.infrastructure.jpa.RankJpaRepository;
import kr.hhplus.be.ecommerce.product.infrastructure.jdbc.RankJdbcTemplateRepository;
import kr.hhplus.be.ecommerce.product.infrastructure.redis.RankRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RankCoreRepository implements RankRepository {

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
