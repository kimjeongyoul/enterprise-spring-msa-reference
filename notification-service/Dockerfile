FROM eclipse-temurin:17-jdk-focal
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "/app.jar"]
