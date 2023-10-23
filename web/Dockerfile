FROM node:14.5.0-stretch-slim



WORKDIR /app
COPY ./web/frontend ./

RUN npm install
RUN npm run build

EXPOSE 3000
CMD ["npm", "start"]

