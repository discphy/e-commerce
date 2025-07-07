package kr.hhplus.be.ecommerce.lock.domain;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LockIdHolder {

    private final ThreadLocal<Map<String, String>> holder = ThreadLocal.withInitial(HashMap::new);

    public void set(String key, String id) {
        Map<String, String> map = holder.get();

        if (exists(key)) {
            throw new IllegalStateException("이미 동일한 키에 대한 락을 보유 중입니다: " + key);
        }

        map.put(key, id);
    }

    public String get(String key) {
        return holder.get().get(key);
    }

    public boolean notExists(String key) {
        return !exists(key);
    }

    public void remove(String key) {
        Map<String, String> map = holder.get();
        map.remove(key);

        if (map.isEmpty()) {
            holder.remove();
        }
    }

    public void clear() {
        holder.remove();
    }

    private boolean exists(String key) {
        Map<String, String> map = holder.get();
        return map.containsKey(key) && map.get(key) != null;
    }
}
