FROM gradle:6.1.1-jdk8 as builder

RUN apt-get update && apt-get install -y build-essential zlib1g-dev

COPY . /app
WORKDIR /app

RUN gradle --no-daemon --build-cache --info --full-stacktrace --configure-on-demand downloadGraalTooling

RUN gradle --no-daemon --build-cache --info --full-stacktrace nativeImage

FROM gcr.io/distroless/java:8

COPY --from=builder /app/build/graal/damcli /app/

ENTRYPOINT ["/app/damcli"]


