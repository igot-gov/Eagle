docker build --no-cache -f ./Dockerfile.build -t notifications-module-build .
docker run --name notifications-module notifications-module-build:latest && docker cp notifications-module:/opt/target/Notification-0.0.1-SNAPSHOT.jar .

docker rm -f notifications-module
docker rmi -f notifications-module-build

docker build --no-cache -t <DockerRepo>/lex-notification-service:<TagName> .
docker push <DockerRepo>/lex-notification-service:<TagName>
