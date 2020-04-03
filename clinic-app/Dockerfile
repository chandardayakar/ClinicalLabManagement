### STAGE 1: Build ###
FROM node AS build
WORKDIR /usr/src/app
COPY clinic-app/package.json ./
RUN npm install
COPY clinic-app/. .
RUN npm run build

### STAGE 2: Run ###
FROM nginx
COPY --from=build /usr/src/app/dist/clinic-app /usr/share/nginx/html
