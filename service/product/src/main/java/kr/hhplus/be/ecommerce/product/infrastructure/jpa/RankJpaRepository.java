package kr.hhplus.be.ecommerce.product.infrastructure.jpa;

import kr.hhplus.be.ecommerce.product.domain.rank.Rank;
import kr.hhplus.be.ecommerce.product.domain.rank.RankType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RankJpaRepository extends JpaRepository<Rank, Long> {
    
    List<Rank> findByRankTypeAndRankDate(RankType rankType, LocalDate rankDate);
}
