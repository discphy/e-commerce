val redissonVersion = "3.27.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.redisson:redisson-spring-boot-starter:${redissonVersion}")
}
