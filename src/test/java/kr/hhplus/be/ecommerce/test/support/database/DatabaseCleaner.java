package kr.hhplus.be.ecommerce.test.support.database;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Profile("test")
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager em;

    private final List<String> tables = new ArrayList<>();

    @PostConstruct
    public void initTables() {
        em.getMetamodel().getEntities().stream()
            .filter(this::isEntity)
            .map(this::getTableName)
            .forEach(tables::add);
    }

    @Transactional
    public void clean() {
        em.flush();
        em.clear();

        // FK 제약 조건 비활성화
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        // 테이블 데이터 삭제
        for (String tableName : tables) {
            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        // FK 제약 조건 활성화
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    private boolean isEntity(EntityType<?> entityType) {
        return entityType.getJavaType().isAnnotationPresent(Entity.class);
    }

    private String getTableName(EntityType<?> entityType) {
        Table tableAnnotation = entityType.getJavaType().getAnnotation(Table.class);

        return Optional.ofNullable(tableAnnotation)
            .filter(this::isNotEmptyTableName)
            .map(Table::name)
            .orElse(toSnakeCaseEntityName(entityType));
    }

    private boolean isNotEmptyTableName(Table table) {
        return !table.name().isEmpty();
    }

    private String toSnakeCaseEntityName(EntityType<?> entityType) {
        return entityType.getName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
