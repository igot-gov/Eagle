FROM node:10
WORKDIR /usr/src/app
COPY package.json /usr/src/app
RUN touch app.properties
RUN yarn
COPY . /usr/src/app
EXPOSE 3000
CMD ["npm" ,"start"]

