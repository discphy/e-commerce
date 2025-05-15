package kr.hhplus.be.ecommerce.domain.rank;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RankKeyTest {

    @DisplayName("날짜로 키를 생성한다.")
    @Test
    void ofDate() {
        // given
        RankType sell = RankType.SELL;
        LocalDate date = LocalDate.of(2025, 5, 13);

        // when
        RankKey rankKey = RankKey.ofDate(sell, date);

        // then
        assertThat(rankKey.generate()).isEqualTo("rank:sell:20250513");
    }

    @DisplayName("일수로 키를 생성한다.")
    @Test
    void ofDays() {
        // given
        RankType sell = RankType.SELL;
        int days = 5;

        // when
        RankKey rankKey = RankKey.ofDays(sell, days);

        // then
        assertThat(rankKey.generate()).isEqualTo("rank:sell:5days");
    }

}