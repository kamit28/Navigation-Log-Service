# syntax=docker/dockerfile:1

# Create a stage for resolving and downloading dependencies.
FROM eclipse-temurin:21-jdk-jammy as deps

RUN mkdir /navlog-service

COPY . /navlog-service

WORKDIR /navlog-service

RUN ./gradlew clean build -x test --no-build-cache --no-daemon

# Run
FROM eclipse-temurin:21-jre-jammy AS final

RUN mkdir /app

COPY --from=deps /navlog-service/build/libs/navlog-service-0.0.1-SNAPSHOT.jar /app/app.jar

RUN rm -rf /navlog-service

# Create a non-privileged user that the app will run under.
# See https://docs.docker.com/go/dockerfile-user-best-practices/
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

WORKDIR /app

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]
