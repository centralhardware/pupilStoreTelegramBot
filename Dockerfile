FROM maven:3.6.3-openjdk-16 as maven

COPY ./pom.xml ./pom.xml

RUN mvn dependency:go-offline -B

COPY ./src ./src

RUN mvn package

FROM openjdk:16-alpine

WORKDIR /znatokiBot

COPY --from=maven target/znatokiStudentBot-1.0-SNAPSHOT.jar .

RUN apk update && \
    apk add --no-cache tzdata curl

ENV TZ Asia/Novosibirsk

HEALTHCHECK --interval=5s --timeout=10s --retries=3 CMD curl -sS 127.0.0.1:8080/healthcheck || exit 1

CMD ["java", "-jar", "znatokiStudentBot-1.0-SNAPSHOT.jar" ]