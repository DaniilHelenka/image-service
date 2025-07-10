FROM eclipse-temurin:21-jre

WORKDIR /app

COPY build/libs/image-service-0.0.1-SNAPSHOT.jar app.jar

# Добавляем wait-for-it.sh
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

EXPOSE 8082

ENTRYPOINT ["/wait-for-it.sh", "postgres:5432", "--timeout=60", "--", "/wait-for-it.sh", "localstack:4566", "--timeout=60", "--", "java", "-jar", "app.jar"]