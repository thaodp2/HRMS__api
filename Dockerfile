FROM maven:3.5-jdk-8-alpine as builder

COPY . /app

WORKDIR /app

RUN mvn -f /app/pom.xml clean package

FROM openjdk:8-jre-alpine as runner

RUN apk add --update \
    curl \
    openssl \
    tzdata \
    && rm -rf /var/cache/apk/*

WORKDIR /app

RUN chmod -R 755 /app/

COPY --from=builder /app/target/HRMS__api-0.0.1-SNAPSHOT.jar /app/HRMS__api.jar
ENTRYPOINT ["java", "-Dlog4j2.formatMsgNoLookups=false", "-Dspring.profiles.active=", "-jar", "HRMS__api.jar"]
# Launch the verticle
#ENTRYPOINT java $JAVA_OPTS -Dlog4j2.formatMsgNoLookups=true -jar /app/HRMS__api.jar
