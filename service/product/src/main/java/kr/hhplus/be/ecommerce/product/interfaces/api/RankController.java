package kr.hhplus.be.ecommerce.product.interfaces.api;

import kr.hhplus.be.ecommerce.product.domain.rank.RankCommand;
import kr.hhplus.be.ecommerce.product.domain.rank.RankInfo;
import kr.hhplus.be.ecommerce.product.domain.rank.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @GetMapping("/api/v1/products/ranks")
    public ApiResponse<RankResponse.PopularProducts> getPopularProducts() {
        RankInfo.PopularProducts popularProducts = rankService.cachedPopularProducts(RankCommand.PopularProducts.ofTop5Days3(LocalDate.now()));
        return ApiResponse.success(RankResponse.PopularProducts.of(popularProducts));
    }
}
