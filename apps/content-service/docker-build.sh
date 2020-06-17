#!/bin/bash

docker build -t eagle-docker.tarento.com/content-service:gold .

docker push eagle-docker.tarento.com/content-service:gold
