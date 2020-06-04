#!/bin/bash

docker build -t <DockerRepo>/content-service:<TagName> .

docker push <DockerRepo>/content-service:<TagName>
