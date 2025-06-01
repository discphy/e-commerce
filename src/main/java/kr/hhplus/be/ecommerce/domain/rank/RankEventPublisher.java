package kr.hhplus.be.ecommerce.domain.rank;

public interface RankEventPublisher {

    void created(RankEvent.Created event);
}
