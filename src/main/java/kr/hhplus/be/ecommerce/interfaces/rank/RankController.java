package kr.hhplus.be.ecommerce.interfaces.rank;

import kr.hhplus.be.ecommerce.application.rank.RankCriteria;
import kr.hhplus.be.ecommerce.application.rank.RankFacade;
import kr.hhplus.be.ecommerce.application.rank.RankResult;
import kr.hhplus.be.ecommerce.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankController {

    private final RankFacade rankFacade;

    @GetMapping("/api/v1/products/ranks")
    public ApiResponse<RankResponse.PopularProducts> getPopularProducts() {
        RankResult.PopularProducts popularProducts = rankFacade.getPopularProducts(RankCriteria.PopularProducts.ofTop5Days3());
        return ApiResponse.success(RankResponse.PopularProducts.of(popularProducts));
    }
}
