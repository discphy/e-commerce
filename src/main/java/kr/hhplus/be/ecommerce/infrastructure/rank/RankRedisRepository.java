package kr.hhplus.be.ecommerce.infrastructure.rank;

import kr.hhplus.be.ecommerce.domain.rank.Rank;
import kr.hhplus.be.ecommerce.domain.rank.RankCommand;
import kr.hhplus.be.ecommerce.domain.rank.RankInfo;
import kr.hhplus.be.ecommerce.domain.rank.RankKeys;
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

    public List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.Query command) {
        String targetKey = command.getTarget().generate();
        RankKeys sources = command.getSources();

        redisTemplate.opsForZSet().unionAndStore(sources.getFirstKey(), sources.getOtherKeys(), targetKey);
        Set<TypedTuple<Long>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores(targetKey, 0, command.getTop() - 1);

        return Optional.ofNullable(tuples)
            .map(this::getList)
            .orElse(new ArrayList<>());
    }

    private List<RankInfo.PopularProduct> getList(Set<TypedTuple<Long>> set) {
        return set.stream()
            .map(this::toPopularProduct)
            .toList();
    }

    private RankInfo.PopularProduct toPopularProduct(TypedTuple<Long> tuple) {
        Long productId = tuple.getValue();
        Long score = Optional.ofNullable(tuple.getScore()).map(Double::longValue).orElse(0L);

        return RankInfo.PopularProduct.of(productId, score);
    }
}
