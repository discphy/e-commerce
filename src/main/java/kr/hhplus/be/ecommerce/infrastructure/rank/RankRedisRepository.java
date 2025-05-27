package kr.hhplus.be.ecommerce.infrastructure.rank;

import kr.hhplus.be.ecommerce.domain.rank.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RankRedisRepository {

    private final RedisTemplate<String, Long> redisTemplate;

    public Rank save(Rank rank) {
        String key = rank.toKey().generate();
        redisTemplate.opsForZSet().incrementScore(key, rank.getProductId(), rank.getScore());

        return rank;
    }

    public List<RankInfo.ProductScore> findPopularSellRanks(RankCommand.Query command) {
        String targetKey = command.getTarget().generate();
        RankKeys sources = command.getSources();

        redisTemplate.opsForZSet().unionAndStore(sources.getFirstKey(), sources.getOtherKeys(), targetKey);
        Set<TypedTuple<Long>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores(targetKey, 0, command.getTop() - 1);

        return Optional.ofNullable(tuples)
            .map(this::getList)
            .orElse(new ArrayList<>());
    }

    public List<RankInfo.ProductScore> findDailyRank(RankKey key) {
        Set<TypedTuple<Long>> tuples = redisTemplate.opsForZSet().rangeWithScores(key.generate(), 0, -1);
        return Optional.ofNullable(tuples)
            .map(this::getList)
            .orElse(new ArrayList<>());
    }

    public boolean delete(RankKey key) {
        return redisTemplate.delete(key.generate());
    }

    private List<RankInfo.ProductScore> getList(Set<TypedTuple<Long>> set) {
        return set.stream()
            .map(this::toPopularProduct)
            .toList();
    }

    private RankInfo.ProductScore toPopularProduct(TypedTuple<Long> tuple) {
        Long productId = tuple.getValue();
        Long score = Optional.ofNullable(tuple.getScore()).map(Double::longValue).orElse(0L);

        return RankInfo.ProductScore.of(productId, score);
    }
}
