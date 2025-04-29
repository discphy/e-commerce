package kr.hhplus.be.ecommerce.domain.rank;

import java.util.List;

public interface RankRepository {

    Rank save(Rank rank);

    List<RankInfo.PopularProduct> findPopularSellRanks(RankCommand.PopularSellRank command);
}
