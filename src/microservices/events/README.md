# Kafka Service with Spring Boot

Простой сервис на Spring Boot, который создает и обрабатывает события в Kafka топиках.

## Архитектура

Сервис включает в себя:
- **Producer** - создает события в Kafka топики
- **Consumer** - читает и обрабатывает события из топиков
- **REST API** - для создания событий через HTTP запросы
- **3 типа событий**: User, Payment, Movie

## Структура проекта

```
kafka-service/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/kafkaservice/
│       │       ├── KafkaServiceApplication.java
│       │       ├── controller/
│       │       │   └── EventController.java
│       │       ├── model/
│       │       │   ├── User.java
│       │       │   ├── Payment.java
│       │       │   └── Movie.java
│       │       └── service/
│       │           ├── EventProducer.java
│       │           └── EventConsumer.java
│       └── resources/
│           └── application.yml
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## API Endpoints

### Новые эндпоинты (согласно OpenAPI спецификации):
- `GET /api/events/health` - проверка здоровья сервиса (возвращает JSON)
- `POST /api/events/user` - создание события пользователя
- `POST /api/events/payment` - создание события платежа  
- `POST /api/events/movie` - создание события фильма

### Устаревшие эндпоинты (для обратной совместимости):
- `POST /api/events/users` - создание события пользователя (deprecated)
- `POST /api/events/payments` - создание события платежа (deprecated)
- `POST /api/events/movies` - создание события фильма (deprecated)

## Kafka Топики

- `user-events` - события пользователей
- `payment-events` - события платежей
- `movie-events` - события фильмов

## Запуск

### 1. С помощью Docker Compose

```bash
# Запуск всех сервисов
docker-compose up -d

# Проверка логов
docker-compose logs kafka-service
```

### 2. Локальная разработка

```bash
# Запуск только Kafka и зависимостей
docker-compose up -d zookeeper kafka kafka-ui

# Запуск Spring Boot приложения
./mvnw spring-boot:run
```

## Тестирование

### 1. Импорт Postman коллекции
Импортируйте файл `Kafka Service Tests.postman_collection.json` в Postman.

### 2. Примеры запросов

**Создание пользователя:**
```bash
curl -X POST http://localhost:8080/api/events/users \
  -H "Content-Type: application/json" \
  -d '{
    "id": "user-001",
    "name": "John Doe", 
    "email": "john.doe@example.com"
  }'
```

**Создание платежа:**
```bash
curl -X POST http://localhost:8080/api/events/payments \
  -H "Content-Type: application/json" \
  -d '{
    "id": "payment-001",
    "userId": "user-001",
    "amount": 99.99,
    "currency": "USD"
  }'
```

**Создание фильма:**
```bash
curl -X POST http://localhost:8080/api/events/movies \
  -H "Content-Type: application/json" \
  -d '{
    "id": "movie-001",
    "title": "The Matrix",
    "genre": "Sci-Fi", 
    "year": 1999
  }'
```

## Мониторинг

- **Kafka UI**: http://localhost:8090 - веб интерфейс для мониторинга Kafka топиков
- **Логи сервиса**: `docker-compose logs kafka-service -f`

## Что происходит при запросе

1. HTTP запрос поступает в `EventController`
2. Контроллер вызывает соответствующий метод `EventProducer`
3. Producer отправляет событие в Kafka топик
4. `EventConsumer` автоматически получает событие и обрабатывает его
5. Логи обработки записываются в консоль

## Проверка работы

1. Запустите сервисы: `docker-compose up -d`
2. Откройте Kafka UI: http://localhost:8090
3. Выполните несколько HTTP запросов через Postman
4. Проверьте в Kafka UI, что сообщения появились в топиках
5. Проверьте логи сервиса: `docker-compose logs kafka-service`

Вы должны увидеть логи отправки и получения событий в консоли сервиса.