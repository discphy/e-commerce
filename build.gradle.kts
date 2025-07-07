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

allprojects {
	repositories {
		mavenCentral()
	}

	apply(plugin = "java")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "org.asciidoctor.jvm.convert")

	java {
		toolchain {
			languageVersion = JavaLanguageVersion.of(17)
		}
	}

	dependencyManagement {
		imports {
			mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
		}
	}

	dependencies {
		// Log
		implementation("ch.qos.logback:logback-classic")

		// lombok
		compileOnly("org.projectlombok:lombok")
		annotationProcessor("org.projectlombok:lombok")
		testCompileOnly("org.projectlombok:lombok")
		testAnnotationProcessor("org.projectlombok:lombok")

		// Test
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.springframework.boot:spring-boot-testcontainers")
		testImplementation("org.testcontainers:junit-jupiter")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
		systemProperty("user.timezone", "UTC")
	}
}