dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // DB
    runtimeOnly("com.mysql:mysql-connector-j")

    // TestContainers
    testImplementation("org.testcontainers:mysql")

    // Common
    implementation(project(":common:client"))

    // RestDocs
    testImplementation(project(":support:rest-docs"))
}