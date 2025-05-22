package kr.hhplus.be.ecommerce.domain.rank;

import kr.hhplus.be.ecommerce.domain.product.Product;

import java.time.LocalDate;
import java.util.List;

public interface RankRepository {

    Rank save(Rank rank);

    List<RankInfo.ProductScore> findProductScores(RankCommand.Query command);

    List<RankInfo.ProductScore> findDailyRank(RankKey key);

    List<Rank> findBy(RankType rankType, LocalDate date);

    void saveAll(List<Rank> ranks);

    boolean delete(RankKey key);

    Product findProductById(Long productId);
}
