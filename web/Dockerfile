FROM node:14.5.0-stretch-slim  as build



WORKDIR /app

ENV PATH /app/node_modules/.bin:$PATH
COPY ./frontend/package.json ./
COPY ./frontend/package-lock.json ./

RUN npm ci
RUN npm install react-scripts@3.4.1 -g

COPY ./frontend ./
RUN npm run build


FROM nginx:stable-alpine-slim
COPY --from=build /app/build /usr/share/nginx/html
COPY ./nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]

