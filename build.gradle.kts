plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

val snippetsDir by extra { file("build/generated-snippets") }
val asciidoctorExt: Configuration by configurations.creating

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

val querydslVersion = "5.0.0"
val restAssuredVersion = "5.3.2"
val redissonVersion = "3.27.1"

dependencies {
    // Spring
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// QueryDSL
	implementation("com.querydsl:querydsl-jpa:${querydslVersion}:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:${querydslVersion}:jakarta")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")

	// Redisson
	implementation("org.redisson:redisson-spring-boot-starter:${redissonVersion}")

	// lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

    // DB
	runtimeOnly("com.mysql:mysql-connector-j")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// RestDocs
	asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

	// RestAssured
	testImplementation("io.rest-assured:rest-assured:${restAssuredVersion}")
	testImplementation("io.rest-assured:json-path:${restAssuredVersion}")
	testImplementation("io.rest-assured:json-schema-validator:${restAssuredVersion}")
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

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
	outputs.dir(snippetsDir)
}

tasks {
	asciidoctor {
		dependsOn(test)
		configurations("asciidoctorExt")
		sources {
			include("**/index.adoc")
		}
		baseDirFollowsSourceFile()
		inputs.dir(snippetsDir)
	}
	bootJar {
		dependsOn(asciidoctor)
		from("build/docs/asciidoc") {
			into("static/docs")
		}
	}
}