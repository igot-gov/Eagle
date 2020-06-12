docker build --no-cache -f ./Dockerfile.build -t authoring-service-build .

docker run --name authoring-build authoring-service-build:latest && docker cp authoring-build:/opt/target/wingspan-authoring-services-0.0.1-SNAPSHOT.jar .
docker rm -f authoring-build
docker rmi -f authoring-service-build

docker build --no-cache -t eagle-docker.tarento.com/lex-sb-ext-authtool-service:gold .
docker push eagle-docker.tarento.com/lex-sb-ext-authtool-service:gold
