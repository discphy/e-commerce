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

    // TestContainers
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:kafka:1.19.1")

    // Common
    implementation(project(":common:cache"))
    implementation(project(":common:message"))
    implementation(project(":common:outbox"))
    implementation(project(":common:storage"))

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

// Clean 태스크가 Q클래스 생성 디렉토리를 확실히 삭제하도록 설정
tasks.named("clean") {
    doLast {
        file(querydslDir).deleteRecursively()
    }
}
