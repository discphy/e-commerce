package kr.hhplus.be.ecommerce.product.infrastructure.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.ecommerce.product.domain.product.ProductCommand;
import kr.hhplus.be.ecommerce.product.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.product.domain.product.ProductSellingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.ecommerce.product.domain.product.QProduct.product;
import static kr.hhplus.be.ecommerce.product.domain.stock.QStock.stock;


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
                cursor(command),
                product.id.in(command.getIds())
            )
            .orderBy(product.id.desc())
            .limit(command.getPageSize())
            .fetch();
    }

    private BooleanExpression cursor(ProductCommand.Query command) {
        return Optional.ofNullable(command.getCursor())
            .map(product.id::lt)
            .orElse(null);
    }
}
