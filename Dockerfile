FROM openjdk:20-ea-17-jdk-slim

EXPOSE 8080
# 파일을 빌드한다
COPY build/libs/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]