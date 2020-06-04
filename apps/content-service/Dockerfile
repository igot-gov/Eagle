FROM node:10
WORKDIR /usr/src/app
COPY . .

RUN cd /usr/src/app/File_server && npm install --only=production

ADD https://github.com/Yelp/dumb-init/releases/download/v1.1.1/dumb-init_1.1.1_amd64 /usr/local/bin/dumb-init
RUN chmod +x /usr/local/bin/dumb-init

WORKDIR /usr/src/app/File_server
EXPOSE 5903
CMD ["dumb-init", "npm", "start"]

