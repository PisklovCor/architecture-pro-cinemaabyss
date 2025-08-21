# Proxy Service (API Gateway)

API Gateway для маршрутизации запросов между микросервисами и монолитом в архитектуре CinemaAbyss.

## Описание

Proxy сервис реализует паттерн API Gateway на базе Spring Cloud Gateway и обеспечивает:
- Маршрутизацию запросов к соответствующим сервисам
- Поддержку градуальной миграции от монолита к микросервисам
- Балансировку нагрузки при постепенном переводе трафика

## Технический стек

- **Java**: 17
- **Spring Boot**: 3.2.0
- **Spring Cloud Gateway**: 2023.0.0
- **Spring Boot Actuator**: для мониторинга
- **Maven**: система сборки
- **Docker**: контейнеризация

## Архитектура маршрутизации

### Стандартные маршруты
- `/api/movies` → movies-service (http://movies-service:8081)
- `/api/events/**` → events-service (http://events-service:8082)
- `/**` → monolith (http://monolith:8080) - fallback для всех остальных запросов

### Градуальная миграция
При включении режима градуальной миграции (`GRADUAL_MIGRATION=true`):
- Часть трафика `/api/movies` направляется на микросервис согласно `MOVIES_MIGRATION_PERCENT`
- Остальной трафик продолжает поступать на монолит
- Используется механизм weighted routing для распределения нагрузки

## Конфигурация

### Переменные окружения

| Переменная | Значение по умолчанию | Описание |
|------------|----------------------|----------|
| `PORT` | 8000 | Порт сервиса |
| `MONOLITH_URL` | http://monolith:8080 | URL монолита |
| `MOVIES_SERVICE_URL` | http://movies-service:8081 | URL movies сервиса |
| `EVENTS_SERVICE_URL` | http://events-service:8082 | URL events сервиса |
| `GRADUAL_MIGRATION` | false | Включение градуальной миграции |
| `MOVIES_MIGRATION_PERCENT` | 0 | Процент трафика на микросервис (0-100) |

### Файл application.yaml
```yaml
server:
  port: ${PORT:8000}

spring:
  application:
    name: proxy

logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    reactor.netty.http.client: DEBUG
```

## Запуск

### Локальный запуск
```bash
mvn spring-boot:run
```

### Docker
```bash
# Сборка образа
docker build -t proxy-service .

# Запуск контейнера
docker run -p 8000:8000 proxy-service
```

### Docker Compose
Сервис интегрирован в общую Docker Compose конфигурацию проекта.

## Мониторинг

### Health Check
- URL: `http://localhost:8000/actuator/health`
- Проверяет доступность сервиса каждые 30 секунд

### Actuator Endpoints
- `/actuator/health` - состояние приложения
- `/actuator/gateway/routes` - текущие маршруты
- `/actuator/metrics` - метрики приложения

## Тестирование

### Запуск тестов
```bash
mvn test
```

### Типы тестов
- **GatewayConfigTest** - проверка стандартной конфигурации маршрутов
- **GatewayConfigGradualMigrationTest** - тестирование градуальной миграции

## Примеры использования

### Обычная маршрутизация
```bash
# Запрос к movies сервису
curl http://localhost:8000/api/movies

# Запрос к events сервису  
curl http://localhost:8000/api/events/upcoming

# Запрос к монолиту (fallback)
curl http://localhost:8000/api/users
```

### Градуальная миграция
```bash
# Включение миграции с 30% трафика на микросервис
docker run -e GRADUAL_MIGRATION=true -e MOVIES_MIGRATION_PERCENT=30 proxy-service
```

## Безопасность

- Контейнер запускается от непривилегированного пользователя `spring`
- Используется Alpine Linux для минимизации поверхности атак
- Health check встроен в Docker контейнер

## Логирование

Включено DEBUG логирование для:
- Spring Cloud Gateway - отслеживание маршрутизации
- Reactor Netty HTTP Client - мониторинг HTTP запросов

## Структура проекта

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── ApiGatewayApplication.java     # Главный класс приложения
│   │   └── configuration/
│   │       └── GatewayConfig.java         # Конфигурация маршрутов
│   └── resources/
│       └── application.yaml               # Конфигурация приложения
└── test/
    └── java/org/example/configuration/
        ├── GatewayConfigTest.java         # Тесты маршрутизации
        └── GatewayConfigGradualMigrationTest.java  # Тесты миграции
```

## Разработка

### Добавление нового маршрута
1. Открыть `GatewayConfig.java`
2. Добавить новый маршрут в метод `customRouteLocator`
3. Обновить переменные окружения при необходимости
4. Добавить тесты для нового маршрута

### Отладка
- Используйте DEBUG логирование для трассировки запросов
- Проверяйте маршруты через `/actuator/gateway/routes`
- Мониторьте состояние целевых сервисов




