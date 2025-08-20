## Пошаговое описание команд
```shell
cd .\src\kubernetes\
```

## Старт миникуба
```shell
minikube start
```

## Доп настройка ingress

```shell
minikube addons enable ingress
```

### tunnel-отдельная консоль <--
```shell
minikube tunnel
```

### dashboard-отдельная консоль <--
```shell
minikube dashboard
```

### Установка/Проверка Helm-chart (Задание 4)
```bash
helm install cinemaabyss .\src\kubernetes\helm --namespace cinemaabyss --create-namespace
helm uninstall cinemaabyss -n cinemaabyss
helm list --namespace cinemaabyss
kubectl get pods -n cinemaabyss
```
----

### Создание namespace
```bash
kubectl apply -f src/kubernetes/namespace.yaml
```

### Создание секретов и переменных
```bash
kubectl apply -f src/kubernetes/configmap.yaml
kubectl apply -f src/kubernetes/secret.yaml
kubectl apply -f src/kubernetes/dockerconfigsecret.yaml
kubectl apply -f src/kubernetes/postgres-init-configmap.yaml
 ```

### Создание базы данных
```bash
kubectl apply -f src/kubernetes/postgres.yaml
```

### Проверка
```bash
kubectl -n cinemaabyss get pod
```
### Создание Kafka
```bash
kubectl apply -f src/kubernetes/kafka/kafka.yaml
```

### Создание монолита
```bash
kubectl apply -f src/kubernetes/monolith.yaml
 ```

### Создание микросервисов:
```bash
kubectl apply -f src/kubernetes/movies-service.yaml
kubectl apply -f src/kubernetes/events-service.yaml
```

### Создание прокси-сервис
```bash
kubectl apply -f src/kubernetes/proxy-service.yaml
```

### После запуска и поднятия подов вывод команды
```bash
kubectl -n cinemaabyss get pod
```

### Создание ingress
```bash
kubectl apply -f src/kubernetes/ingress.yaml
```

### Удаление всего окружения после завершения работы
```shell
minikube delete --all
```
