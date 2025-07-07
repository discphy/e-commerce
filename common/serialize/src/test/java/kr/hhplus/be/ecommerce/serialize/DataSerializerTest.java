package kr.hhplus.be.ecommerce.serialize;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DataSerializerTest {

    @DisplayName("문자열을 역직렬화 한다.")
    @Test
    void deserializeWithStr() {
        // given
        String json = "{\"name\":\"test\",\"age\":30}";

        // when
        TestData data = DataSerializer.deserialize(json, TestData.class);

        // then
        assertThat(data.getName()).isEqualTo("test");
        assertThat(data.getAge()).isEqualTo(30);
    }

    @DisplayName("객체를 역직렬화 한다.")
    @Test
    void deserializeWithObj() {
        // given
        Map<String, Object> testData = new HashMap<>();
        testData.put("name", "test");
        testData.put("age", 30);

        // when
        TestData data = DataSerializer.deserialize(testData, TestData.class);

        // then
        assertThat(data.getName()).isEqualTo("test");
        assertThat(data.getAge()).isEqualTo(30);
    }

    @DisplayName("객체를 직렬화 한다.")
    @Test
    void serialize() {
        // given
        TestData data = new TestData();
        data.setName("test");
        data.setAge(30);

        // when
        String json = DataSerializer.serialize(data);

        // then
        assertThat(json).isEqualTo("{\"name\":\"test\",\"age\":30}");
    }

    static class TestData {

        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

}