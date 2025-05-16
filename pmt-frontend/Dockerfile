# Build Angular app
FROM node:18-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build -- --configuration production

# Serve built app with Nginx
FROM nginx:alpine

# Copy Angular build output to Nginx html directory
COPY --from=builder /app/dist/pmt-frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
