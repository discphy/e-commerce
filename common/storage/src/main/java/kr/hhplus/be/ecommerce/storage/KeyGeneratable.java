package kr.hhplus.be.ecommerce.storage;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kr.hhplus.be.ecommerce.storage.KeyConstant.KEY_NAMESPACE_SEPARATOR;


public interface KeyGeneratable {

    KeyType type();

    List<String> namespaces();

    default String generate() {
        return Stream.concat(
            Stream.of(type().getKey()),
            namespaces().stream()
        ).collect(Collectors.joining(KEY_NAMESPACE_SEPARATOR));
    }
}
