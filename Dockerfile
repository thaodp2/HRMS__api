FROM asia.gcr.io/minswap-devops/openjdk:8-jre-alpine

RUN apk add --update \
    curl \
    openssl \
    tzdata \
    && rm -rf /var/cache/apk/*

RUN cp -r -f /usr/share/zoneinfo/Asia/Saigon /etc/localtime

COPY trust-certs/* /usr/local/share/ca-certificates/
RUN chmod -R 755 /usr/local/share/ca-certificates/
RUN update-ca-certificates 

# Set the location of the verticles
WORKDIR /usr/app

COPY temp/*.jar /usr/app/app.jar
COPY src/main/resources/monitor/* /usr/app/conf/
RUN ls -l /usr/app/conf/
COPY start.sh /usr/app/

RUN chmod -R 755 /usr/app/

# Launch the verticle
ENTRYPOINT java $JAVA_OPTS -Dlog4j2.formatMsgNoLookups=true -jar app.jar