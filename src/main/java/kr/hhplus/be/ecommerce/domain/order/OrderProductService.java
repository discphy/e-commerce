package kr.hhplus.be.ecommerce.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public OrderProductInfo.TopPaidProducts getTopPaidProducts(OrderProductCommand.TopOrders command) {
        List<OrderProduct> orderProducts = orderProductRepository.findOrderIdsIn(command.getOrderIds());

        Map<Long, Integer> productQuantityMap = orderProducts.stream()
            .collect(Collectors.groupingBy(
                OrderProduct::getProductId,
                Collectors.summingInt(OrderProduct::getQuantity)
            ));

        List<Long> sortedProductIds = productQuantityMap.entrySet().stream()
            .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .toList();

        return OrderProductInfo.TopPaidProducts.of(sortedProductIds);
    }
}
