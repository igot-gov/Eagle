git pull origin node-social-share
docker build -t 10.0.1.129:5000/social-share:bronze .
docker push 10.0.1.129:5000/social-share:bronze
