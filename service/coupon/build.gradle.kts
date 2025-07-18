val querydslVersion = "5.0.0"

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")

    // DB
    runtimeOnly("com.mysql:mysql-connector-j")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:${querydslVersion}:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:${querydslVersion}:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Common
    implementation(project(":common:storage"))
    implementation(project(":common:message"))
    implementation(project(":common:client"))

    // TestContainers
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:kafka:1.19.1")

    // RestDocs
    testImplementation(project(":support:rest-docs"))
}

val querydslDir = "$buildDir/generated/querydsl"

sourceSets {
    main {
        java {
            srcDir(querydslDir)
        }
    }
}

tasks.withType<JavaCompile> {
    options.annotationProcessorGeneratedSourcesDirectory = file(querydslDir)
}
