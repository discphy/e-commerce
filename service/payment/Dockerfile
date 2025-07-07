FROM openjdk:17-jdk-slim
COPY build/libs/payment.jar payment.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "payment.jar"]

