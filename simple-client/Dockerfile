FROM docker.io/library/eclipse-temurin:17-jammy AS builder
LABEL maintainer="Kwangil Ha <kwangil77@hotmail.com>"
ARG DB_HOST=${DB_HOST:-127.0.0.1}
ARG BUILD_DIR=/src/simple-client/build
ARG GRADLE_OPTS
ENV GRADLE_OPTS=${GRADLE_OPTS}
WORKDIR /src
COPY . .
RUN ./gradlew clean schemaRegistryDownload :simple-client:bootJar --console=plain \
    && mkdir ${BUILD_DIR}/extracted \
    && java -Djarmode=layertools -jar ${BUILD_DIR}/libs/simple-client.jar extract --destination ${BUILD_DIR}/extracted

FROM docker.io/library/eclipse-temurin:17-jre-jammy
LABEL maintainer="Kwangil Ha <kwangil77@hotmail.com>"
ARG BUILD_DIR=/src/simple-client/build
COPY --from=builder ${BUILD_DIR}/extracted/dependencies/ ./
COPY --from=builder ${BUILD_DIR}/extracted/spring-boot-loader/ ./
COPY --from=builder ${BUILD_DIR}/extracted/snapshot-dependencies/ ./
COPY --from=builder ${BUILD_DIR}/extracted/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
