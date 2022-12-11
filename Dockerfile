FROM maven:3.8.6-openjdk-11 as builder
ENV HOME=/app
RUN mkdir -p $HOME
RUN mkdir -p /root/.m2 \
    mkdir -p /root/.m2/repository
ADD settings.xml /root/.m2
WORKDIR $HOME
ADD pom.xml $HOME
RUN mvn verify --fail-never
ADD . $HOME
RUN mvn -X -Dmaven.test.skip=true clean package

FROM openjdk:11-jre as runner

RUN apt install \
    curl \
    openssl \
    tzdata \
    && rm -rf /var/cache/apk/*

WORKDIR /app

RUN chmod -R 755 /app/

COPY --from=builder /app/src/main/resources/templateexcel /app/templateexcel
COPY --from=builder /app/target/HRMS__api-0.0.1-SNAPSHOT.jar /app/HRMS__api.jar
ENTRYPOINT ["java", "-Dlog4j2.formatMsgNoLookups=false", "-Dspring.profiles.active=prod", "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=6699,suspend=n", "-jar", "HRMS__api.jar"]
# Launch the verticle
#ENTRYPOINT java $JAVA_OPTS -Dlog4j2.formatMsgNoLookups=true -jar /app/HRMS__api.jar
