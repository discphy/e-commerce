package kr.hhplus.be.ecommerce.domain.rank;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;

    public List<Rank> createSellRank(RankCommand.CreateList command) {
        return command.getRanks().stream()
            .map(this::createSell)
            .map(rankRepository::save)
            .toList();
    }

    public RankInfo.PopularProducts getPopularSellRank(RankCommand.PopularSellRank command) {
        List<RankInfo.PopularProduct> popularProducts = rankRepository.findPopularSellRanks(command);
        return RankInfo.PopularProducts.of(popularProducts);
    }

    private Rank createSell(RankCommand.Create command) {
        return Rank.createSell(command.getProductId(), command.getRankDate(), command.getScore());
    }
}
