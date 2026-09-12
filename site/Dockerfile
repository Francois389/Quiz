FROM nginx:alpine

COPY index.html /usr/share/nginx/html/index.html
COPY style.css /usr/share/nginx/html/style.css
COPY app.js /usr/share/nginx/html/app.js
COPY generate-config.sh /docker-entrypoint.d/30-generate-config.sh

RUN chmod +x /docker-entrypoint.d/30-generate-config.sh

EXPOSE 80
