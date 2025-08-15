# Используем многоэтапную сборку для оптимизации размера образа
FROM eclipse-temurin:17-jdk-alpine as builder

# Устанавливаем рабочую директорию
WORKDIR /src/microservices

# Копируем файлы для сборки
COPY pom.xml .
COPY src ./src

# Устанавливаем Maven
RUN apk add --no-cache maven

# Собираем приложение
RUN mvn clean package -DskipTests

# Второй этап - создание финального образа
FROM eclipse-temurin:17-jre-alpine

# Создаем пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring

# Устанавливаем curl для health check
RUN apk add --no-cache curl

# Устанавливаем рабочую директорию
WORKDIR /src/microservices

# Копируем jar файл из предыдущего этапа
COPY --from=builder /src/microservices/target/events-*.jar app.jar

# Меняем владельца файлов
RUN chown spring:spring app.jar

# Переключаемся на пользователя spring
USER spring:spring

# Открываем порт 8090
EXPOSE 8090

# Добавляем health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8090/actuator/health || exit 1

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]