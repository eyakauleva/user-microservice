FROM node:20
WORKDIR /
COPY package*.json /
RUN npm install
COPY index.js /
COPY src /src
EXPOSE 9000
CMD [ "node", "index.js" ]