FROM maven:3.9.0 as builder
COPY . /app
WORKDIR /app
RUN --mount=type=cache,target=/root/.m2  mvn -pl -web clean install

FROM openjdk:21-jdk-slim
WORKDIR /app
RUN printenv
COPY --from=builder /app/main/target/fitsinn-main-*.jar ./app.jar

RUN chown 1001 ./app.jar \
    && chmod "g+rwX" ./app.jar

USER 1001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
