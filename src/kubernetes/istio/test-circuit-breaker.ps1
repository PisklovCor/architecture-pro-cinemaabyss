# PowerShell script for testing Istio Circuit Breaker configuration
# This script deploys fortio and tests circuit breaker behavior

param(
    [string]$Namespace = "cinemaabyss"
)

$ErrorActionPreference = "Stop"

Write-Host "=== Testing Istio Circuit Breaker Configuration ===" -ForegroundColor Green
Write-Host "Namespace: $Namespace" -ForegroundColor Yellow
Write-Host ""

# Check if namespace exists and has Istio injection enabled
Write-Host "1. Checking namespace configuration..." -ForegroundColor Cyan
try {
    $istioInjection = kubectl get namespace $Namespace -o jsonpath='{.metadata.labels.istio-injection}' 2>$null
    if ($istioInjection -ne "enabled") {
        throw "Istio injection not enabled"
    }
    Write-Host "✓ Namespace $Namespace has Istio injection enabled" -ForegroundColor Green
}
catch {
    Write-Host "Error: Namespace $Namespace not found or Istio injection not enabled" -ForegroundColor Red
    Write-Host "Please run: kubectl label namespace $Namespace istio-injection=enabled" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# Deploy Fortio for load testing
Write-Host "2. Deploying Fortio load testing tool..." -ForegroundColor Cyan
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.25/samples/httpbin/sample-client/fortio-deploy.yaml -n $Namespace

# Wait for Fortio to be ready
Write-Host "Waiting for Fortio deployment to be ready..." -ForegroundColor Yellow
kubectl rollout status deployment/fortio-deploy -n $Namespace --timeout=120s

# Get Fortio pod name
$fortioPod = kubectl get pod -n $Namespace -l app=fortio -o jsonpath='{.items[0].metadata.name}'
Write-Host "✓ Fortio pod: $fortioPod" -ForegroundColor Green
Write-Host ""

# Test movies-service circuit breaker
Write-Host "3. Testing movies-service circuit breaker..." -ForegroundColor Cyan
Write-Host "Sending high load to trigger circuit breaker (50 concurrent connections, 500 requests)..." -ForegroundColor Yellow
Write-Host ""

kubectl exec -n $Namespace $fortioPod -c fortio -- fortio load -c 50 -qps 0 -n 500 -loglevel Warning http://movies-service:8081/api/movies

Write-Host ""
Write-Host "4. Checking circuit breaker statistics..." -ForegroundColor Cyan

# Check Istio proxy statistics
Write-Host "Movies-service circuit breaker stats:" -ForegroundColor Yellow
kubectl exec -n $Namespace $fortioPod -c istio-proxy -- pilot-agent request GET stats | Select-String "movies-service" | Select-String -Pattern "(pending|overflow|ejected)" | Select-Object -First 10

Write-Host ""
Write-Host "=== Test completed ===" -ForegroundColor Green
Write-Host ""
Write-Host "Expected behavior:" -ForegroundColor Cyan
Write-Host "- Code 200: Normal successful responses" -ForegroundColor White
Write-Host "- Code 503: Circuit breaker activated (service unavailable)" -ForegroundColor White
Write-Host "- upstream_rq_pending_overflow: Number of requests rejected by circuit breaker" -ForegroundColor White
Write-Host ""

# Test monolith circuit breaker
Write-Host "5. Testing monolith circuit breaker..." -ForegroundColor Cyan
Write-Host "Sending moderate load to test monolith circuit breaker..." -ForegroundColor Yellow
Write-Host ""

kubectl exec -n $Namespace $fortioPod -c fortio -- fortio load -c 30 -qps 0 -n 300 -loglevel Warning http://monolith:8080/api/users

Write-Host ""
Write-Host "Monolith circuit breaker stats:" -ForegroundColor Yellow
kubectl exec -n $Namespace $fortioPod -c istio-proxy -- pilot-agent request GET stats | Select-String "monolith" | Select-String -Pattern "(pending|overflow|ejected)" | Select-Object -First 10

Write-Host ""
Write-Host "=== All tests completed ===" -ForegroundColor Green
Write-Host "For detailed analysis, check the response codes and circuit breaker statistics above." -ForegroundColor Cyan
