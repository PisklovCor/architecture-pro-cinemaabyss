## Задание 1
[Контейнерная диаграмма To‑Be (C4)](diagrams/C4_Container.png)

## Задание 2

### 1. Proxy

Сервис [Proxy](src/microservices/proxy/README.md)

### 2. Kafka
Сервис [Events](src/microservices/events/README.md)

----

Тесты Postman: [Запуск коллекции в postman](docs/тесты.png)

Тесты Newman: [Запуск коллекции в newman](docs/ньюмэн.png)

Kafka: [Kafka UI](docs/кафка.png)


## Задание 3

Команды для последовательного запуска вынесены в файл [README.md](src/kubernetes/README.md)

### CI/CD

Собранные [образы](https://github.com/PisklovCor?tab=packages)

### Proxy и Events в Kubernetes

Вызов [movies](docs/movies_k8s.png)

Тесты [postman](docs/postman_k8s.png)

Логи [events](docs/events_log.png)

## Задание 4
### Для корректной работы необходимо отключить Istio и включить Ingress через настройки в файле: [values.yaml](src/kubernetes/helm/values.yaml)
```
# Ingress configuration
ingress:
    enabled: true
---
# Istio configuration
istio:
    enabled: false
```

Описание установки и настройки [README.md](src/kubernetes/README.md)

Скриншот [Helm](docs/helm-deploy.png)

Запрос [http://cinemaabyss.example.com/api/movies](docs/helm-get.png)

## Задание 5
### Для корректной работы необходимо отключить Ingress и включить Istio через настройки в файле: [values.yaml](src/kubernetes/helm/values.yaml)
```
# Ingress configuration
ingress:
    enabled: false
---
# Istio configuration
istio:
    enabled: true
```

Описание установки и настройки [Istion](src/kubernetes/istio/ISTIO_DEPLOYMENT.md)

Скриншот запуска тестов [fortio](docs/istion_test.png)