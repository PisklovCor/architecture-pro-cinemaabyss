# Развертывание Istio с Circuit Breaker для CinemaAbyss

Данная документация описывает процесс развертывания Istio Service Mesh с настройкой Circuit Breaker паттернов для повышения надежности и безопасности системы CinemaAbyss.

## Предварительные требования

- Kubernetes кластер (minikube, kind, или полноценный кластер)
- Helm 3.x
- kubectl
- istioctl (опционально для расширенной диагностики)

## Пошаговое развертывание

### 0. Запуск minikube 

```bash
minikube start
```

### 1. Установка Istio

```bash
# Добавление Helm репозитория Istio
helm repo add istio https://istio-release.storage.googleapis.com/charts
helm repo update

# Установка базовых компонентов Istio
helm install istio-base istio/base -n istio-system --set defaultRevision=default --create-namespace

# Установка Istio Gateway
helm install istio-ingressgateway istio/gateway -n istio-system

# Установка Istio Control Plane
helm install istiod istio/istiod -n istio-system --wait
```

### 2. Развертывание приложения с Istio

```bash
# Развертывание CinemaAbyss через Helm с поддержкой Istio
helm install cinemaabyss ./src/kubernetes/helm --namespace cinemaabyss --create-namespace

# Включение Istio injection для namespace
kubectl label namespace cinemaabyss istio-injection=enabled --overwrite

# Проверка статуса namespace
kubectl get namespace -L istio-injection
```

### 3. Применение Circuit Breaker конфигурации

```bash
# Применение DestinationRules и VirtualService для Circuit Breaker
kubectl apply -f ./src/kubernetes/circuit-breaker-config.yaml -n cinemaabyss
```

### 4. Проверка развертывания

```bash
# Проверка статуса подов (должны содержать istio-proxy sidecar)
kubectl get pods -n cinemaabyss

# Проверка Istio конфигурации
kubectl get destinationrules,virtualservices,gateways -n cinemaabyss
```

## Конфигурация Circuit Breaker

### Настройки для Movies Service

- **maxConnections**: 10 соединений
- **http1MaxPendingRequests**: 3 (пониженный порог для быстрого срабатывания)
- **consecutiveGatewayErrors**: 2 (быстрое обнаружение проблем)
- **baseEjectionTime**: 30s

### Настройки для Monolith

- **maxConnections**: 10 соединений  
- **http1MaxPendingRequests**: 5
- **consecutiveGatewayErrors**: 3
- **baseEjectionTime**: 30s

## Тестирование Circuit Breaker

### Автоматическое тестирование

```bash
# Запуск автоматизированного скрипта тестирования
./src/kubernetes/istio/test-circuit-breaker.sh
```

### Ручное тестирование

```bash
# Развертывание Fortio для нагрузочного тестирования
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.25/samples/httpbin/sample-client/fortio-deploy.yaml -n cinemaabyss

# Получение имени пода Fortio
FORTIO_POD=$(kubectl get pod -n cinemaabyss | grep fortio | awk '{print $1}')

# Тестирование movies-service (высокая нагрузка)
kubectl exec -n cinemaabyss $FORTIO_POD -c fortio -- fortio load \
    -c 50 -qps 0 -n 500 -loglevel Warning \
    http://movies-service:8081/api/movies

# Тестирование monolith (умеренная нагрузка)
kubectl exec -n cinemaabyss $FORTIO_POD -c fortio -- fortio load \
    -c 30 -qps 0 -n 300 -loglevel Warning \
    http://monolith:8080/api/users
```

### Проверка статистики Circuit Breaker

```bash
# Статистика для movies-service
kubectl exec -n cinemaabyss $FORTIO_POD -c istio-proxy -- pilot-agent request GET stats | \
    grep movies-service | grep pending

# Статистика для monolith
kubectl exec -n cinemaabyss $FORTIO_POD -c istio-proxy -- pilot-agent request GET stats | \
    grep monolith | grep pending
```

## Ожидаемые результаты

При успешной работе Circuit Breaker вы увидите:

```bash
IP addresses distribution:
10.106.113.46:8081: 421
Code 200 : 79 (15.8 %)    # Успешные запросы
Code 500 : 22 (4.4 %)     # Ошибки сервера  
Code 503 : 399 (79.8 %)   # Circuit breaker сработал
```

Статистика Istio покажет:
```bash
cluster.outbound|8081||movies-service.cinemaabyss.svc.cluster.local.upstream_rq_pending_total: 311
cluster.outbound|8081||movies-service.cinemaabyss.svc.cluster.local.upstream_rq_pending_overflow: 21
```

## Мониторинг и отладка

### Kiali Dashboard (опционально)

```bash
# Установка Kiali для визуализации
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.25/samples/addons/kiali.yaml
kubectl rollout status deployment/kiali -n istio-system
kubectl port-forward svc/kiali 20001:20001 -n istio-system
```

### Jaeger Tracing (опционально)

```bash
# Установка Jaeger для трейсинга
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.25/samples/addons/jaeger.yaml
kubectl port-forward svc/tracing 8080:80 -n istio-system
```

## Конфигурация через Helm

Все настройки Istio можно изменить в `values.yaml`:

```yaml
istio:
  enabled: true
  circuitBreaker:
    moviesService:
      maxConnections: 10
      http1MaxPendingRequests: 3
      consecutiveGatewayErrors: 2
```

## Канареечный деплой

Для канареечного деплоя используйте VirtualService с весовой маршрутизацией:

```yaml
http:
- match:
  - headers:
      canary:
        exact: "true"
  route:
  - destination:
      host: movies-service
      subset: v2
- route:
  - destination:
      host: movies-service
      subset: v1
    weight: 90
  - destination:
      host: movies-service
      subset: v2
    weight: 10
```

## Очистка

```bash
# Удаление всей инфраструктуры
istioctl uninstall --purge
kubectl delete namespace istio-system
kubectl delete all --all -n cinemaabyss
kubectl delete namespace cinemaabyss
minikube delete --all
```

## Полезные команды

```bash
# Проверка конфигурации Istio
istioctl analyze -n cinemaabyss

# Проверка прокси конфигурации
istioctl proxy-config cluster $FORTIO_POD.cinemaabyss

# Просмотр логов Istio
kubectl logs -l app=istiod -n istio-system

# Проверка статуса mesh
istioctl proxy-status
```
