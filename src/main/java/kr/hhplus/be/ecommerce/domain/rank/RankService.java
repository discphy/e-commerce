package kr.hhplus.be.ecommerce.domain.rank;

import kr.hhplus.be.ecommerce.domain.product.Product;
import kr.hhplus.be.ecommerce.support.cache.CacheType.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;
    private final RankEventPublisher rankEventPublisher;

    public List<Rank> createSellRank(RankCommand.CreateList command) {
        List<Rank> ranks = command.getRanks().stream()
            .map(this::createSell)
            .map(rankRepository::save)
            .toList();

        RankEvent.Created event = RankEvent.Created.of(ranks);
        rankEventPublisher.created(event);

        return ranks;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.POPULAR_PRODUCT, key = "'top:' + #command.top + ':days:' + #command.days")
    public RankInfo.PopularProducts cachedPopularProducts(RankCommand.PopularProducts command) {
        return getPopularProducts(command);
    }

    @Transactional(readOnly = true)
    @CachePut(value = CacheName.POPULAR_PRODUCT, key = "'top:' + #command.top + ':days:' + #command.days")
    public RankInfo.PopularProducts updatedPopularProducts(RankCommand.PopularProducts command) {
        return getPopularProducts(command);
    }

    @Transactional(readOnly = true)
    public RankInfo.PopularProducts getPopularProducts(RankCommand.PopularProducts command) {
        RankKey target = RankKey.ofDays(RankType.SELL, command.getDays());
        RankKeys sources = RankKeys.ofDaysWithDate(RankType.SELL, command.getDays(), command.getDate());

        RankCommand.Query query = RankCommand.Query.of(command.getTop(), target, sources);
        List<RankInfo.PopularProduct> productScores = rankRepository.findProductScores(query)
            .stream()
            .map(this::getProduct)
            .toList();

        return RankInfo.PopularProducts.of(productScores);
    }

    @Transactional
    public void persistDailyRank(LocalDate date) {
        RankKey key = RankKey.ofDate(RankType.SELL, date);
        List<RankInfo.ProductScore> productScores = rankRepository.findDailyRank(key);

        if (productScores.isEmpty()) {
            log.info("일일 판매 링크가 존재하지 않습니다. date: {}", date);
            return;
        }

        List<Rank> ranks = productScores.stream()
            .map(ps -> Rank.createSell(ps.getProductId(), date, ps.getTotalScore()))
            .toList();

        rankRepository.saveAll(ranks);
        rankRepository.delete(key);
    }

    private Rank createSell(RankCommand.Create command) {
        return Rank.createSell(command.getProductId(), command.getRankDate(), command.getScore());
    }

    private RankInfo.PopularProduct getProduct(RankInfo.ProductScore productScore) {
        Product product = rankRepository.findProductById(productScore.getProductId());
        return RankInfo.PopularProduct.of(product);
    }
}
