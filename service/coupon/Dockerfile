FROM openjdk:17-jdk-slim
COPY build/libs/coupon.jar coupon.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "coupon.jar"]

