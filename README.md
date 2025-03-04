# Melbourne Weather Service

## Overview
This is a Spring Boot-based HTTP service that provides Melbourne weather updates by fetching data from WeatherStack and OpenWeatherMap. The service prioritizes WeatherStack and falls back to OpenWeatherMap in case of failures. It also caches weather data for 3 seconds to improve performance and ensure availability in case of provider outages.

## Features
- Fetches weather data (temperature and wind speed) from WeatherStack.
- Automatic failover to OpenWeatherMap if WeatherStack is down.
- Caches responses for 3 seconds to reduce API calls and improve efficiency.
- Provides stale data if both providers are down.
- Uses RESTful architecture with structured JSON responses.
- Implements SOLID principles for maintainability and scalability.

## Thought Process & Considerations
- **Reliability:** Failover mechanism ensures that users always get weather data even if the primary provider fails.
- **Scalability:** Caching reduces external API requests, preventing rate limits from being hit.
- **Maintainability:** Clear separation of concerns (controller, service, external API handling) allows easy modifications.
- **Extensibility:** New providers can be added with minimal changes.
- **Observability:** Logs errors when an API call fails, aiding debugging and monitoring.
- **Testability:** JUnit tests ensure robustness and prevent regressions.

## Folder Structure
```
src/main/java/com/melbourne/weather/
│── config/
│   ├── CacheConfig.java
│   ├── AppConfig.java
│── controller/
│   ├── WeatherController.java
│── exception/
│   ├── GlobalExceptionHandler.java
│   ├── WeatherServiceException.java
│── model/
│   ├── OpenWeatherResponse.java
│   ├── WeatherResponse.java
│── provider/
│   ├── WeatherProvider.java
│   ├── WeatherStackProvider.java
│   ├── OpenWeatherMapProvider.java
│── service/
│   ├── WeatherService.java
│── WeatherServiceApplication.java
```

## Trade-offs
- **Scheduled Cache Refresh:** Ensures that responses remain available even if both providers fail.
- **Using RESTTemplate:** Could be replaced with WebClient for non-blocking async calls, but RESTTemplate is simpler and sufficient for this use case.

## How to Run
### Prerequisites
- Java 
- Maven
- API keys for WeatherStack and OpenWeatherMap

### Steps
1. Clone the repository.
2. Navigate to the project directory.
3. Set API keys in environment variable for OPEN_WEATHER_API_KEY and WEATHER_STACK_API_KEY
4. Run the application:
   ```sh
   mvn spring-boot:run
   ```
5. Access the service:
   ```sh
   curl http://localhost:8080/weather
   ```

### Example Response
```json
{
  "current": {
    "temperature": 19,
    "wind_speed": 6
  }
}
```

## Testing
JUnit tests are included for:
- `WeatherService`
- `WeatherStackProvider`
- `OpenWeatherMapProvider`
- `WeatherController`

Run tests with:
```sh
mvn test
```

## Future Improvements
- Implement async non-blocking API calls.
- Enhance error handling with better logging and alerts.
