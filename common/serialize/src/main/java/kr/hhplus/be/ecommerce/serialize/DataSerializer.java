package kr.hhplus.be.ecommerce.serialize;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataSerializer {

    private static final ObjectMapper MAPPER = initialize();

    private static ObjectMapper initialize() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 역직렬화, 없는 필드 존재시 무시
    }

    public static <T> T deserialize(String data, Class<T> type) {
        try {
            return MAPPER.readValue(data, type);
        } catch (Exception e) {
            log.error("[Serializer.deserialize] JSON 역직렬화 실패: data={} type={}", data, type, e);
            return null;
        }
    }

    public static <T> T deserialize(Object data, Class<T> type) {
        return MAPPER.convertValue(data, type);
    }

    public static String serialize(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error("[Serializer.serialize] JSON 직렬화 실패: object={}", object, e);
            return null;
        }
    }
}
