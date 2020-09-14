docker build --no-cache -f ./Dockerfile.build -t recommendation-service-build .
docker run --name recommendation-build recommendation-service-build:latest && docker cp recommendation-build:/opt/target/recommendation-services-0.0.1-SNAPSHOT.jar .
docker rm -f recommendation-build
docker rmi -f recommendation-service-build
docker build --no-cache -t 10.0.1.129:5000/sb-recommendation-service:bronze .
docker push 10.0.1.129:5000/sb-recommendation-service:bronze
