FROM openjdk:17-jdk-slim
COPY build/libs/balance.jar balance.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "balance.jar"]

