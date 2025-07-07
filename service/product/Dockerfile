FROM openjdk:17-jdk-slim
COPY build/libs/product.jar product.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "product.jar"]

