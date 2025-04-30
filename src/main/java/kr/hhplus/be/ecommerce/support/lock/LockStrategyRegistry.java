package kr.hhplus.be.ecommerce.support.lock;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LockStrategyRegistry {

    private final Map<LockStrategy, LockTemplate> strategies;

    public LockStrategyRegistry(List<LockTemplate> templates) {
        this.strategies = templates.stream()
            .collect(Collectors.toMap(LockTemplate::getLockStrategy, template -> template));
    }

    public LockTemplate getLockTemplate(LockStrategy lockStrategy) {
        return strategies.get(lockStrategy);
    }
}
