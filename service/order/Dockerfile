FROM openjdk:17-jdk-slim
COPY build/libs/order.jar order.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "order.jar"]

