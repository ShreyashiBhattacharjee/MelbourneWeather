package com.melbourne.weather.provider;

import com.melbourne.weather.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherStackProvider implements WeatherProvider {
    @Value("${weather.stack.api.url}")
    private String API_URL;
    private RestTemplate restTemplate;

    public WeatherStackProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public WeatherResponse getWeather() {
        try {
            return restTemplate.getForObject(API_URL, WeatherResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Weather stack provider unavailable at the moment.", e);
        }
    }
}