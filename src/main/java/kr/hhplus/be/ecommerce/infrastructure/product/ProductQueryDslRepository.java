package kr.hhplus.be.ecommerce.infrastructure.product;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.ecommerce.domain.product.ProductCommand;
import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.product.ProductSellingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.ecommerce.domain.product.QProduct.product;
import static kr.hhplus.be.ecommerce.domain.stock.QStock.stock;

@Repository
@RequiredArgsConstructor
public class ProductQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<ProductInfo.Product> findBySellStatusIn(List<ProductSellingStatus> statuses) {
        return queryFactory.select(
                Projections.constructor(
                    ProductInfo.Product.class,
                    product.id,
                    product.name,
                    product.price,
                    stock.quantity
                )
            )
            .from(product)
            .leftJoin(stock).on(product.id.eq(stock.productId))
            .where(product.sellStatus.in(statuses))
            .fetch();
    }

    public List<ProductInfo.Product> findAll(ProductCommand.Query command) {
        return queryFactory.select(
                Projections.constructor(
                    ProductInfo.Product.class,
                    product.id,
                    product.name,
                    product.price,
                    stock.quantity
                )
            )
            .from(product)
            .leftJoin(stock).on(product.id.eq(stock.productId))
            .where(
                product.sellStatus.in(command.getStatus()),
                Optional.ofNullable(command.getCursor())
                    .map(product.id::lt)
                    .orElse(null)
            )
            .orderBy(product.id.desc())
            .limit(command.getPageSize())
            .fetch();
    }
}
