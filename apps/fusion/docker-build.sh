#!/bin/bash

docker build -t <DockerRepo>/ui-static:<TagName> .

echo "docker build is completed !!!! Starting docker push"

docker push <DockerRepo>/ui-static:<TagName> .

