FROM maven:3.5-jdk-8-alpine as builder
ENV HOME=/app
RUN mkdir -p $HOME
RUN mkdir -p /root/.m2 \
    mkdir -p /root/.m2/repository
ADD settings.xml /root/.m2
WORKDIR $HOME
ADD pom.xml $HOME
RUN mvn verify --fail-never
ADD . $HOME
RUN mvn -X clean package

FROM openjdk:8-jre-alpine as runner

RUN apk add --update \
    curl \
    openssl \
    tzdata \
    && rm -rf /var/cache/apk/*

WORKDIR /app

RUN chmod -R 755 /app/

COPY --from=builder /app/target/HRMS__api-0.0.1-SNAPSHOT.jar /app/HRMS__api.jar
ENV VIRTUAL_HOST="api.ms-hrms.software"
ENV VIRTUAL_PORT=6689
ENV LETSENCRYPT_HOST="api.ms-hrms.software"
ENV LETSENCRYPT_EMAIL="thaodphe141294@fpt.edu.vn"
EXPOSE 6689
ENTRYPOINT ["java", "-Dlog4j2.formatMsgNoLookups=false", "-Dspring.profiles.active=prod", "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=6699,suspend=n", "-jar", "HRMS__api.jar"]
# Launch the verticle
#ENTRYPOINT java $JAVA_OPTS -Dlog4j2.formatMsgNoLookups=true -jar /app/HRMS__api.jar
