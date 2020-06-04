#!/bin/bash

docker build -t <DockerRepo>/social-forum:<TagName> .

docker push <DockerRepo>/social-forum:<TagName>
