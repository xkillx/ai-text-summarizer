# AI Text Summarizer API

A production-ready RESTful API for intelligent text summarization using OpenAI's GPT models.

## Features

- **Multiple Summary Styles**: Concise, bullet points, and executive summaries
- **Input Validation**: Comprehensive validation with custom sanitization
- **Security**: Rate limiting, input sanitization, and prompt injection protection
- **Resilience**: Automatic retry with exponential backoff and timeout handling
- **Observability**: Metrics, health checks, and structured logging
- **Documentation**: Interactive Swagger UI and comprehensive API docs
- **Production Ready**: Docker support, Kubernetes manifests, and environment-specific configs

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.9+
- OpenAI API Key

### Running Locally

```bash
# Clone the repository
git clone https://github.com/xkillx/ai-text-summarizer
cd ai-text-summarizer

# Set your OpenAI API key
export OPENAI_API_KEY=sk-proj-...

# Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## Usage

### Example Request

```bash
curl -X POST http://localhost:8080/api/v1/summarize \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Artificial intelligence (AI) is intelligence demonstrated by machines, as opposed to the natural intelligence displayed by humans or animals. Leading AI textbooks define the field as the study of intelligent agents: any system that perceives its environment and takes actions that maximize its chance of achieving its goals. Some popular applications of AI include machine learning, natural language processing, computer vision, robotics, and autonomous vehicles.",
    "maxLength": 150,
    "summaryStyle": "CONCISE"
  }'
```

### Example Response

```json
{
  "summary": "AI is machine intelligence that studies systems perceiving and acting to achieve goals.",
  "inputLength": 523,
  "summaryLength": 95,
  "model": "gpt-4o-mini",
  "processingTimeMs": 856
}
```

## Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

## Deployment

### Docker

```bash
# Build the image
docker build -t ai-text-summarizer:1.0.0 .

# Run the container
docker run -d \
  --name ai-text-summarizer \
  -p 8080:8080 \
  -e OPENAI_API_KEY=sk-proj-... \
  ai-text-summarizer:1.0.0
```

### Docker Compose

```bash
# Create .env file with OPENAI_API_KEY
echo "OPENAI_API_KEY=sk-proj-..." > .env

# Start the services
docker-compose up -d
```

See [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) for complete deployment guide.

## Configuration

### Environment Variables

| Variable                    | Description           | Default       |
| --------------------------- | --------------------- | ------------- |
| `OPENAI_API_KEY`            | OpenAI API key        | Required      |
| `SPRING_PROFILES_ACTIVE`    | Active profile        | `prod`        |
| `AI_SUMMARIZER_MODEL`       | Model to use          | `gpt-4o-mini` |
| `AI_SUMMARIZER_TEMPERATURE` | Temperature (0.0-2.0) | `0.3`         |

See [docs/ENVIRONMENT_VARIABLES.md](docs/ENVIRONMENT_VARIABLES.md) for all options.

## API Endpoints

### POST /api/v1/summarize

Generate a summary of the provided text.

**Request Body:**

```json
{
  "text": "Text to summarize (min 100 characters)",
  "maxLength": 150,
  "summaryStyle": "CONCISE"
}
```

**Summary Styles:**

- `CONCISE`: Brief paragraph-style summary
- `BULLET`: Key points as bullet list
- `EXECUTIVE`: High-level strategic summary

**Response Codes:**

- `200`: Success
- `400`: Invalid input
- `429`: Rate limit exceeded
- `503`: LLM service unavailable

## Development

### Running Tests

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Project Structure

```
src/main/java/com/azharkhalid/aitextsummarizer/
├── config/           # Spring configuration classes
├── controller/       # REST controllers
├── dto/              # Data Transfer Objects
├── enums/            # Enumerations
├── exception/        # Custom exceptions
├── health/           # Health indicators
├── metrics/          # Custom metrics
├── resilience/       # Resilience4j configuration
├── service/          # Business logic
└── util/             # Utility classes
```

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **AI**: Spring AI with OpenAI GPT models
- **Resilience**: Resilience4j (retry, timeout, rate limiting)
- **Monitoring**: Spring Actuator, Micrometer, Prometheus
- **Documentation**: SpringDoc OpenAPI
- **Testing**: JUnit 5, Mockito, AssertJ
- **Containerization**: Docker, Docker Compose

## License

MIT License - see LICENSE file for details

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## Support

For issues and questions, please open a GitHub issue.
