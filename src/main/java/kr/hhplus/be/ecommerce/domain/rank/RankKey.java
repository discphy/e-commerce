package kr.hhplus.be.ecommerce.domain.rank;

import kr.hhplus.be.ecommerce.support.key.KeyGeneratable;
import kr.hhplus.be.ecommerce.support.key.KeyType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static kr.hhplus.be.ecommerce.support.key.KeyType.RANK;

public class RankKey implements KeyGeneratable {

    private static final String DAYS = "days";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final RankType rankType;
    private final String suffix;

    private RankKey(RankType rankType, String suffix) {
        this.rankType = rankType;
        this.suffix = suffix;
    }

    public static RankKey ofDate(RankType rankType, LocalDate rankDate) {
        return new RankKey(rankType, rankDate.format(formatter));
    }

    public static RankKey ofDays(RankType rankType, int days) {
        return new RankKey(rankType, days + DAYS);
    }

    @Override
    public KeyType type() {
        return RANK;
    }

    @Override
    public List<String> namespaces() {
        return List.of(rankType.name().toLowerCase(), suffix);
    }
}
