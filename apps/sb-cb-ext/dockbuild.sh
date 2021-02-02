docker build --no-cache -f ./Dockerfile.build -t sb-cb-ext-build .

docker run --name sb-cb-ext-build sb-cb-ext-build:latest &&  docker cp sb-cb-ext-build:/opt/target/sb-cb-ext-0.0.1-SNAPSHOT.jar .

docker rm -f sb-cb-ext-build
docker rmi -f sb-cb-ext-build

docker build --no-cache -t eagle-docker.tarento.com/lex-core-service:gold .
docker push eagle-docker.tarento.com/lex-core-service:gold
