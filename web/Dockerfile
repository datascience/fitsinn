FROM node:14.5.0-stretch-slim




COPY ./web/frontend ./

WORKDIR /app
RUN npm install
RUN npm run build

EXPOSE 3000
CMD ["npm", "start"]

