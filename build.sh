#!/bin/bash

docker run --rm -v /root/.m2:/root/.m2 -v $PWD:/code -w /code maven:3.6-jdk-8 -- mvn clean package -Dmaven.test.skip=true
cp target/ew-cashbackservice-0.0.1-SNAPSHOT.jar target/ew-cashbackservice.jar
docker build -f Dockerfile.ci . --tag "10.120.109.16:5000/ew-cashbackservice:latest"
docker push "10.120.109.16:5000/ew-cashbackservice:latest"
