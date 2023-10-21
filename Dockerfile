FROM maven:3.9.0 as builder
COPY . /app
WORKDIR /app
RUN mvn -pl !web clean install -Pprod

FROM openjdk:21-jdk-slim
WORKDIR /app
RUN printenv
COPY --from=builder /app/main/target/procontent-main-*.jar ./app.jar

RUN chown 1001 ./app.jar \
    && chmod "g+rwX" ./app.jar

USER 1001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
