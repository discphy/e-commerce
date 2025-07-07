package kr.hhplus.be.ecommerce.product.domain.rank;

public interface RankEventPublisher {

    void created(RankEvent.Created event);
}
