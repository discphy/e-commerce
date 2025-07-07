package kr.hhplus.be.ecommerce.product.infrastructure.jdbc;

import kr.hhplus.be.ecommerce.product.domain.rank.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RankJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<Rank> ranks) {
        String sql = "INSERT INTO product_rank (product_id, rank_date, rank_type, score) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, ranks, ranks.size(), (ps, rank) -> {
            ps.setLong(1, rank.getProductId());
            ps.setDate(2, Date.valueOf(rank.getRankDate()));
            ps.setString(3, rank.getRankType().name());
            ps.setLong(4, rank.getScore());
        });
    }
}
