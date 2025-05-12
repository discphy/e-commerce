package kr.hhplus.be.ecommerce.support.lock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class LockKeyGenerator {

    private static final String LOCK_PREFIX = "lock:";

    public String generateKey(String[] parameterNames, Object[] args, String key, LockType type) {
        String parseKey = parseKey(parameterNames, args, key);
        return LOCK_PREFIX + type.createKey(parseKey);
    }

    private String parseKey(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        try {
            return parser.parseExpression(key).getValue(context, String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("락 키 설정이 올바르지 않습니다. " + key, e);
        }
    }
}
