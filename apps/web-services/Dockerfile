FROM node:12

WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install --only=production
COPY dist/ .

EXPOSE 8080

CMD [ "node", "index.js" ]

