FROM gradle:6.1.1-jdk8 as builder

RUN apt-get update && apt-get install -y build-essential zlib1g-dev

COPY . /app
WORKDIR /app

RUN gradle --no-daemon --build-cache --info --full-stacktrace cliJar

FROM gcr.io/distroless/java:11

COPY --from=builder /app/build/libs/damcli-*-cli.jar /app/damcli.jar

ENTRYPOINT [ "/usr/bin/java", "-jar", "/app/damcli.jar"]


