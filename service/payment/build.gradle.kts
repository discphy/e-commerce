dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")

    // DB
    runtimeOnly("com.mysql:mysql-connector-j")

    // TestContainers
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:kafka:1.19.1")

    // Common
    implementation(project(":common:message"))
    implementation(project(":common:outbox"))
    implementation(project(":common:client"))
}