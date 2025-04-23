package kr.hhplus.be.ecommerce.infrastructure.rank;

import kr.hhplus.be.ecommerce.domain.rank.Rank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RankJpaRepository extends JpaRepository<Rank, Long> {
}
