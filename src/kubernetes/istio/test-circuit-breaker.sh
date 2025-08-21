#!/bin/bash

# Script for testing Istio Circuit Breaker configuration
# This script deploys fortio and tests circuit breaker behavior

set -e

NAMESPACE="cinemaabyss"
FORTIO_DEPLOYMENT="fortio-deploy"

echo "=== Testing Istio Circuit Breaker Configuration ==="
echo "Namespace: $NAMESPACE"
echo ""

# Check if namespace exists and has Istio injection enabled
echo "1. Checking namespace configuration..."
kubectl get namespace $NAMESPACE -o jsonpath='{.metadata.labels.istio-injection}' 2>/dev/null || {
    echo "Error: Namespace $NAMESPACE not found or Istio injection not enabled"
    echo "Please run: kubectl label namespace $NAMESPACE istio-injection=enabled"
    exit 1
}

echo "✓ Namespace $NAMESPACE has Istio injection enabled"
echo ""

# Deploy Fortio for load testing
echo "2. Deploying Fortio load testing tool..."
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.25/samples/httpbin/sample-client/fortio-deploy.yaml -n $NAMESPACE

# Wait for Fortio to be ready
echo "Waiting for Fortio deployment to be ready..."
kubectl rollout status deployment/$FORTIO_DEPLOYMENT -n $NAMESPACE --timeout=120s

# Get Fortio pod name
FORTIO_POD=$(kubectl get pod -n $NAMESPACE -l app=fortio -o jsonpath='{.items[0].metadata.name}')
echo "✓ Fortio pod: $FORTIO_POD"
echo ""

# Test movies-service circuit breaker
echo "3. Testing movies-service circuit breaker..."
echo "Sending high load to trigger circuit breaker (50 concurrent connections, 500 requests)..."
echo ""

kubectl exec -n $NAMESPACE $FORTIO_POD -c fortio -- fortio load \
    -c 50 \
    -qps 0 \
    -n 500 \
    -loglevel Warning \
    http://movies-service:8081/api/movies

echo ""
echo "4. Checking circuit breaker statistics..."

# Check Istio proxy statistics
echo "Movies-service circuit breaker stats:"
kubectl exec -n $NAMESPACE $FORTIO_POD -c istio-proxy -- pilot-agent request GET stats | \
    grep movies-service | grep -E "(pending|overflow|ejected)" | head -10

echo ""
echo "=== Test completed ==="
echo ""
echo "Expected behavior:"
echo "- Code 200: Normal successful responses"
echo "- Code 503: Circuit breaker activated (service unavailable)"
echo "- upstream_rq_pending_overflow: Number of requests rejected by circuit breaker"
echo ""

# Test monolith circuit breaker
echo "5. Testing monolith circuit breaker..."
echo "Sending moderate load to test monolith circuit breaker..."
echo ""

kubectl exec -n $NAMESPACE $FORTIO_POD -c fortio -- fortio load \
    -c 30 \
    -qps 0 \
    -n 300 \
    -loglevel Warning \
    http://monolith:8080/api/users

echo ""
echo "Monolith circuit breaker stats:"
kubectl exec -n $NAMESPACE $FORTIO_POD -c istio-proxy -- pilot-agent request GET stats | \
    grep monolith | grep -E "(pending|overflow|ejected)" | head -10

echo ""
echo "=== All tests completed ==="
echo "For detailed analysis, check the response codes and circuit breaker statistics above."
