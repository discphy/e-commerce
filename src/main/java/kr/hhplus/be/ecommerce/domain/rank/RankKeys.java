package kr.hhplus.be.ecommerce.domain.rank;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

public class RankKeys {

    private final List<RankKey> keys;

    private RankKeys(List<RankKey> keys) {
        this.keys = keys;
    }

    public static RankKeys ofDaysWithDate(RankType rankType, int days, LocalDate date) {
        List<RankKey> keys = IntStream.rangeClosed(0, days)
            .mapToObj(i -> RankKey.ofDate(rankType, date.minusDays(i)))
            .toList();
        return new RankKeys(keys);
    }

    public String getFirstKey() {
        return keys.get(0).generate();
    }

    public List<String> getOtherKeys() {
        return keys.subList(1, keys.size()).stream()
            .map(RankKey::generate)
            .toList();
    }
}
