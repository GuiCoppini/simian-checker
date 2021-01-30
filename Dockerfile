FROM openjdk:11

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG JAR_FILE=build/libs/simian-checker*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar", "/app.jar"]