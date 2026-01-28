# Deployment Guide

This guide provides step-by-step instructions for deploying the AI Text Summarizer API to various environments.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Local Deployment](#local-deployment)
- [Docker Deployment](#docker-deployment)
- [Production Deployment](#production-deployment)
- [Health Checks](#health-checks)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required

- **Java 17+** (for running locally)
- **Maven 3.9+** (for building)
- **Docker 20.10+** (for containerized deployment)
- **OpenAI API Key** - Get one at https://platform.openai.com/

### Optional

- **Docker Compose** (for local development)
- **Kubernetes** (for orchestration)

---

## Local Deployment

### 1. Build the Application

```bash
# Clone the repository
git clone https://github.com/yourusername/ai-text-summarizer.git
cd ai-text-summarizer

# Build the application
./mvnw clean package

# Run tests
./mvnw test
```

### 2. Set Environment Variables

```bash
# Set required environment variables
export OPENAI_API_KEY=sk-proj-...
export SPRING_PROFILES_ACTIVE=dev
```

### 3. Run the Application

```bash
# Using Maven
./mvnw spring-boot:run

# Or using the JAR file
java -jar target/aitextsummarizer-0.0.1-SNAPSHOT.jar
```

### 4. Verify Deployment

```bash
# Check health endpoint
curl http://localhost:8080/actuator/health

# Test the API
curl -X POST http://localhost:8080/api/v1/summarize \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Artificial intelligence (AI) is intelligence demonstrated by machines, as opposed to the natural intelligence displayed by humans or animals. Leading AI textbooks define the field as the study of intelligent agents: any system that perceives its environment and takes actions that maximize its chance of achieving its goals. Some popular applications of AI include machine learning, natural language processing, computer vision, robotics, and autonomous vehicles.",
    "maxLength": 150,
    "summaryStyle": "CONCISE"
  }'
```

---

## Docker Deployment

### 1. Build Docker Image

```bash
# Build the image
docker build -t ai-text-summarizer:1.0.0 .

# Verify the image was built
docker images | grep ai-text-summarizer
```

### 2. Run Container

```bash
# Run the container
docker run -d \
  --name ai-text-summarizer \
  -p 8080:8080 \
  -e OPENAI_API_KEY=sk-proj-... \
  -e SPRING_PROFILES_ACTIVE=prod \
  ai-text-summarizer:1.0.0

# View logs
docker logs -f ai-text-summarizer
```

### 3. Docker Compose

```bash
# Create .env file
cat > .env << EOF
OPENAI_API_KEY=sk-proj-...
SPRING_PROFILES_ACTIVE=prod
AI_SUMMARIZER_MODEL=gpt-4o-mini
EOF

# Start the services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the services
docker-compose down
```

---

## Production Deployment

### Option 1: Docker in Production

#### Build Production Image

```bash
# Build optimized production image
docker build \
  --build-arg SPRING_PROFILES_ACTIVE=prod \
  -t ai-text-summarizer:prod \
  .

# Tag for registry
docker tag ai-text-summarizer:prod registry.example.com/ai-text-summarizer:1.0.0

# Push to registry
docker push registry.example.com/ai-text-summarizer:1.0.0
```

#### Run Production Container

```bash
docker run -d \
  --name ai-text-summarizer-prod \
  --restart unless-stopped \
  -p 8080:8080 \
  -e OPENAI_API_KEY=${OPENAI_API_KEY} \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus \
  -e AI_SUMMARIZER_MODEL=gpt-4o-mini \
  -e JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0" \
  -v /var/log/ai-text-summarizer:/app/logs \
  ai-text-summarizer:prod
```

### Option 2: Kubernetes Deployment

#### Create Namespace

```bash
kubectl create namespace ai-summarizer
```

#### Create Secret for API Key

```bash
kubectl create secret generic openai-credentials \
  --from-literal=api-key=sk-proj-... \
  --namespace=ai-summarizer
```

#### Deploy Application

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ai-text-summarizer
  namespace: ai-summarizer
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ai-text-summarizer
  template:
    metadata:
      labels:
        app: ai-text-summarizer
    spec:
      containers:
      - name: ai-text-summarizer
        image: registry.example.com/ai-text-summarizer:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: OPENAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: openai-credentials
              key: api-key
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: AI_SUMMARIZER_MODEL
          value: "gpt-4o-mini"
        - name: JAVA_OPTS
          value: "-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: ai-text-summarizer
  namespace: ai-summarizer
spec:
  selector:
    app: ai-text-summarizer
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

```bash
# Apply the deployment
kubectl apply -f deployment.yaml

# Check status
kubectl get pods -n ai-summarizer
kubectl get svc -n ai-summarizer

# View logs
kubectl logs -f deployment/ai-text-summarizer -n ai-summarizer
```

---

## Health Checks

### Docker Health Check

```bash
# Check container health
docker inspect --format='{{.State.Health.Status}}' ai-text-summarizer

# View health check logs
docker inspect --format='{{range .State.Health.Log}}{{.Output}}{{end}}' ai-text-summarizer
```

### Kubernetes Health Checks

```bash
# Check pod status
kubectl get pods -n ai-summarizer

# Describe pod for health check details
kubectl describe pod ai-text-summarizer-xxx -n ai-summarizer
```

### Manual Health Check

```bash
# Basic health check
curl http://localhost:8080/actuator/health

# Detailed health check
curl http://localhost:8080/actuator/health | jq

# Check specific components
curl http://localhost:8080/actuator/health/llm
curl http://localhost:8080/actuator/health/resilience
```

---

## Monitoring

### Metrics Endpoint

```bash
# Get all available metrics
curl http://localhost:8080/actuator/metrics

# Get specific metric
curl http://localhost:8080/actuator/metrics/http.server.requests

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

### Setting Up Prometheus

**prometheus.yml:**

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'ai-text-summarizer'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['ai-text-summarizer:8080']
```

```bash
# Run Prometheus
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus
```

### Setting Up Grafana

1. Import the Spring Boot dashboard
2. Configure Prometheus data source
3. Monitor key metrics:
   - Request rate
   - Response time percentiles
   - Error rate
   - JVM memory usage
   - LLM call duration

---

## Troubleshooting

### Issue: Container fails to start

**Solution:**
```bash
# Check logs
docker logs ai-text-summarizer

# Common issues:
# 1. Missing OPENAI_API_KEY
# 2. Insufficient memory
# 3. Port already in use
```

### Issue: Health check failing

**Solution:**
```bash
# Check health endpoint
curl http://localhost:8080/actuator/health

# Check if LLM is accessible
curl http://localhost:8080/actuator/health/llm

# Verify API key is valid
```

### Issue: High memory usage

**Solution:**
```bash
# Adjust JVM memory settings
export JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"

# Or for Docker:
docker run -e JAVA_OPTS="-Xmx512m -Xms256m" ...
```

### Issue: Slow response times

**Solution:**
```bash
# Check metrics
curl http://localhost:8080/actuator/metrics/http.server.requests

# Adjust timeout settings
export AI_SUMMARIZER_TIMEOUT=45s

# Consider upgrading to faster model
export AI_SUMMARIZER_MODEL=gpt-4o
```

---

## Rolling Updates

### Docker

```bash
# Pull new image
docker pull registry.example.com/ai-text-summarizer:1.1.0

# Stop and remove old container
docker stop ai-text-summarizer
docker rm ai-text-summarizer

# Start new container
docker run -d \
  --name ai-text-summarizer \
  ... \
  registry.example.com/ai-text-summarizer:1.1.0
```

### Kubernetes

```bash
# Update image
kubectl set image deployment/ai-text-summarizer \
  ai-text-summarizer=registry.example.com/ai-text-summarizer:1.1.0 \
  -n ai-summarizer

# Watch rollout status
kubectl rollout status deployment/ai-text-summarizer -n ai-summarizer

# Rollback if needed
kubectl rollout undo deployment/ai-text-summarizer -n ai-summarizer
```

---

## Security Considerations

1. **API Key Security**
   - Never log or expose API keys
   - Use secrets management (Vault, AWS Secrets Manager)
   - Rotate keys regularly

2. **Network Security**
   - Use HTTPS in production
   - Configure firewalls to restrict access
   - Use VPN for private deployments

3. **Container Security**
   - Run as non-root user
   - Scan images for vulnerabilities
   - Keep base images updated

4. **Rate Limiting**
   - Configure appropriate rate limits
   - Monitor for abuse
   - Implement API authentication

---

## Performance Tuning

### JVM Options

```bash
# For containers
JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# For fixed memory
JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"

# For low latency
JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### Connection Pooling

```yaml
# application-prod.yaml
server:
  tomcat:
    threads:
      max: 200
      min-spare: 10
    max-connections: 10000
```

---

## Backup and Recovery

### Volume Mounts

```bash
# Mount logs directory
docker run -v /host/logs:/app/logs ...
```

### Configuration Backup

```bash
# Backup configuration files
tar -czf config-backup-$(date +%Y%m%d).tar.gz \
  application*.yaml \
  .env
```

---

## Additional Resources

- [Spring Boot Production Tips](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.deploying.production)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [OpenAI API Documentation](https://platform.openai.com/docs)
