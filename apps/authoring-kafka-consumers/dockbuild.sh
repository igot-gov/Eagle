#!/bin/bash

docker build --no-cache -f ./Dockerfile.build -t kafka-consumers-build .

docker run --name kafka-consumers-build kafka-consumers-build:latest &&  docker cp kafka-consumers-build:/opt/target/authoring-kafka-consumers-0.0.1-SNAPSHOT.jar .
docker rm -f kafka-consumers-build
docker rmi -f kafka-consumers-build

docker build --no-cache -t <DockerRepo>/wn-kafka-service:<TagName> .
docker push <DockerRepo>/wn-kafka-service:<TagName>
