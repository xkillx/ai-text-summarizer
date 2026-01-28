# Environment Variables Reference

This document lists all environment variables that can be configured for the AI Text Summarizer API.

## Required Environment Variables

### `OPENAI_API_KEY`

**Description:** OpenAI API key for accessing GPT models

**Example:** `sk-proj-abc123...`

**How to Get:**
1. Sign up at https://platform.openai.com/
2. Navigate to API Keys section
3. Create a new API key

**Security Warning:**
- Never commit API keys to version control
- Rotate keys periodically
- Use environment-specific keys for dev/staging/prod

---

## Optional Environment Variables

### Spring Configuration

#### `SPRING_PROFILES_ACTIVE`

**Description:** Active Spring profile (dev, prod, test)

**Default:** `prod`

**Options:** `dev`, `prod`, `test`

**Example:** `export SPRING_PROFILES_ACTIVE=prod`

---

### Summarizer Configuration

#### `AI_SUMMARIZER_MODEL`

**Description:** OpenAI model to use for summarization

**Default:** `gpt-4o-mini`

**Options:** `gpt-4o-mini`, `gpt-4o`, `gpt-3.5-turbo`

**Example:** `export AI_SUMMARIZER_MODEL=gpt-4o-mini`

#### `AI_SUMMARIZER_TEMPERATURE`

**Description:** Temperature parameter for model (0.0 - 2.0)

**Default:** `0.3`

**Range:** `0.0` to `2.0`

**Lower values** produce more focused summaries
**Higher values** produce more creative summaries

**Example:** `export AI_SUMMARIZER_TEMPERATURE=0.3`

#### `AI_SUMMARIZER_MAX_TOKENS`

**Description:** Maximum tokens in the model response

**Default:** `500`

**Range:** `100` to `4096`

**Example:** `export AI_SUMMARIZER_MAX_TOKENS=500`

#### `AI_SUMMARIZER_TIMEOUT`

**Description:** Maximum time to wait for LLM response

**Default:** `30s`

**Format:** Duration (e.g., `30s`, `1m`)

**Example:** `export AI_SUMMARIZER_TIMEOUT=30s`

---

### Resilience Configuration

#### `RESILIENCE4J_RETRY_INSTANCES-SUMMARIZESERVICE-MAX-ATTEMPTS`

**Description:** Maximum number of retry attempts

**Default:** `3`

**Range:** `1` to `10`

**Example:** `export RESILIENCE4J_RETRY_INSTANCES-SUMMARIZESERVICE-MAX-ATTEMPTS=3`

#### `RESILIENCE4J_RETRY_INSTANCES-SUMMARIZESERVICE-WAIT-DURATION`

**Description:** Wait duration between retry attempts

**Default:** `2s`

**Format:** Duration (e.g., `2s`, `500ms`)

**Example:** `export RESILIENCE4J_RETRY_INSTANCES-SUMMARIZESERVICE-WAIT-DURATION=2s`

#### `RESILIENCE4J_TIMELIMITER_INSTANCES-SUMMARIZESERVICE-TIMEOUT-DURATION`

**Description:** Maximum time to wait for the operation to complete

**Default:** `25s`

**Format:** Duration (e.g., `25s`, `30s`)

**Example:** `export RESILIENCE4J_TIMELIMITER_INSTANCES-SUMMARIZESERVICE-TIMEOUT-DURATION=25s`

---

### Rate Limiting Configuration

#### `RESILIENCE4J-RATELIMITER_INSTANCES-SUMMARIZESERVICE-LIMIT-FOR-PERIOD`

**Description:** Maximum number of requests allowed in the time window

**Default:** `100`

**Range:** `1` to `10000`

**Example:** `export RESILIENCE4J-RATELIMITER_INSTANCES-SUMMARIZESERVICE-LIMIT-FOR-PERIOD=100`

#### `RESILIENCE4J-RATELIMITER_INSTANCES-SUMMARIZESERVICE-LIMIT-REFRESH-PERIOD`

**Description:** Time window for rate limiting

**Default:** `1m`

**Format:** Duration (e.g., `1m`, `30s`, `1h`)

**Example:** `export RESILIENCE4J-RATELIMITER_INSTANCES-SUMMARIZESERVICE-LIMIT-REFRESH-PERIOD=1m`

---

### Actuator Configuration

#### `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE`

**Description:** Comma-separated list of actuator endpoints to expose

**Default (prod):** `health,info,metrics,prometheus`
**Default (dev):** `*`

**Options:** `health`, `info`, `metrics`, `prometheus`, `env`, `loggers`, etc.

**Example:** `export MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,metrics,prometheus`

#### `MANAGEMENT_ENDPOINT_HEALTH_SHOW-DETAILS`

**Description:** Whether to show detailed health information

**Default:** `always`

**Options:** `never`, `when-authorized`, `always`

**Example:** `export MANAGEMENT_ENDPOINT_HEALTH_SHOW-DETAILS=always`

---

### JVM Configuration

#### `JAVA_OPTS`

**Description:** JVM options for the application

**Default:** `-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0`

**Common Options:**
- `-Xmx`: Maximum heap size (e.g., `-Xmx1g`)
- `-Xms`: Initial heap size (e.g., `-Xms512m`)
- `-XX:+UseG1GC`: Use G1 garbage collector

**Example:** `export JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"`

---

## Quick Start

### Local Development

```bash
# Set required environment variables
export OPENAI_API_KEY=sk-proj-...
export SPRING_PROFILES_ACTIVE=dev

# Run the application
./mvnw spring-boot:run
```

### Docker

```bash
# Create .env file
cat > .env << EOF
OPENAI_API_KEY=sk-proj-...
SPRING_PROFILES_ACTIVE=prod
AI_SUMMARIZER_MODEL=gpt-4o-mini
EOF

# Run with Docker Compose
docker-compose up -d
```

### Kubernetes

```yaml
# In your Kubernetes manifest
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
```

---

## Security Best Practices

1. **Use secrets management** for sensitive values (API keys, passwords)
2. **Never commit** `.env` files or secrets to version control
3. **Use different API keys** for dev/staging/production environments
4. **Rotate API keys** regularly
5. **Monitor usage** to detect leaked keys
6. **Use environment-specific configurations** for different environments
