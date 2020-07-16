#!/bin/bash

docker build -t eagle-docker.tarento.com/lex-content-service:gold .

docker push eagle-docker.tarento.com/lex-content-service:gold
