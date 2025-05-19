package kr.hhplus.be.ecommerce.domain.rank;

import java.time.LocalDate;
import java.util.List;

public interface RankRepository {

    Rank save(Rank rank);

    List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.Query command);

    List<RankInfo.PopularProduct> findDailyRank(RankKey key);

    List<Rank> findBy(RankType rankType, LocalDate date);

    void saveAll(List<Rank> ranks);

    boolean delete(RankKey key);
}
