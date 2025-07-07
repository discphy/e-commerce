FROM openjdk:17-jdk-slim
COPY build/libs/user.jar user.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "user.jar"]

