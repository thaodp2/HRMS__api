FROM maven:alpine as builder
ADD /home/ec2-user/.m2/ /root/.m2
ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD pom.xml $HOME
RUN mvn verify --fail-never
ADD . $HOME
RUN mvn clean package

FROM openjdk:8-jre-alpine as runner

RUN apk add --update \
    curl \
    openssl \
    tzdata \
    && rm -rf /var/cache/apk/*

WORKDIR /app

RUN chmod -R 755 /app/

COPY --from=builder /app/target/HRMS__api-0.0.1-SNAPSHOT.jar /app/HRMS__api.jar
ENTRYPOINT ["java", "-Dlog4j2.formatMsgNoLookups=false", "-Dspring.profiles.active=prod", "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=6699,suspend=n", "-jar", "HRMS__api.jar"]
# Launch the verticle
#ENTRYPOINT java $JAVA_OPTS -Dlog4j2.formatMsgNoLookups=true -jar /app/HRMS__api.jar
