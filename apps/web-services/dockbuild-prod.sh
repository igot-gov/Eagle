#!/bin/bash

docker build -f ./Dockerfile.build -t web-services.build .
docker run --name=web-services.build web-services.build && docker cp web-services.build:/usr/src/app/dist.zip .

sleep 30
docker rm web-services.build
docker rmi -f web-services.build
unzip dist.zip

docker build -f Dockerfile -t <DockerRepo>/ui-proxies:<TagName> .
docker push <DockerRepo>/ui-proxies:<TagName>

rm -rf dist
rm -rf dist.zip

