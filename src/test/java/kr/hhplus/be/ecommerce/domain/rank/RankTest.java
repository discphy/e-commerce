package kr.hhplus.be.ecommerce.domain.rank;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RankTest {

    @DisplayName("판매 랭크 생성 시, 상품은 필수다.")
    @Test
    void createSellWithoutProductId() {
        // given
        Long productId = null;
        LocalDate rankDate = LocalDate.of(2025, 4, 23);
        long score = 0L;

        // when & then
        assertThatThrownBy(() -> Rank.createSell(productId, rankDate, score))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("상품이 존재하지 않습니다.");
    }

    @DisplayName("판매 랭크 생성 시, 랭크 날짜는 필수다.")
    @Test
    void createSellWithoutRankDate() {
        // given
        Long productId = 1L;
        LocalDate rankDate = null;
        long score = 0L;

        // when & then
        assertThatThrownBy(() -> Rank.createSell(productId, rankDate, score))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("날짜가 존재하지 않습니다.");
    }
}