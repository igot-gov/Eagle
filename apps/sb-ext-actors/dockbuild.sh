docker build --no-cache -f ./Dockerfile.build -t sb-ext-service-build:eagle .

docker run --name sbext-build sb-ext-service-build:eagle && docker cp sbext-build:/opt/target/bodhi-1.0-SNAPSHOT.jar .
docker rm -f sbext-build
docker rmi -f sb-ext-service-build

docker build --no-cache -t eagle-docker.tarento.com/lex-sb-ext-service:gold .
docker push eagle-docker.tarento.com/lex-sb-ext-service:gold
