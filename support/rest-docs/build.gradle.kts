plugins {
    `java-library`
}

val snippetsDir by extra { file("build/generated-snippets") }
val asciidoctorExt: Configuration by configurations.creating

val restAssuredVersion = "5.3.2"

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-test")

    // RestDocs
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
    api("org.springframework.restdocs:spring-restdocs-mockmvc")

    // RestAssured
    api("io.rest-assured:rest-assured:${restAssuredVersion}")
    api("io.rest-assured:json-path:${restAssuredVersion}")
    api("io.rest-assured:json-schema-validator:${restAssuredVersion}")
}

tasks.withType<Test> {
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
