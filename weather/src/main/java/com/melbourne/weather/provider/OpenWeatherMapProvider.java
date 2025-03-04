package com.melbourne.weather.provider;

import com.melbourne.weather.model.OpenWeatherResponse;
import com.melbourne.weather.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OpenWeatherMapProvider implements WeatherProvider {
    @Value("${open.weather.api.url}")
    private String API_URL;
    private RestTemplate restTemplate;

    public OpenWeatherMapProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public WeatherResponse getWeather() {
        try {
            OpenWeatherResponse response = restTemplate.getForObject(API_URL, OpenWeatherResponse.class);
            return convertToWeatherResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("OpenWeatherMap provider unavailable at the moment.", e);
        }
    }

    private WeatherResponse convertToWeatherResponse(OpenWeatherResponse openWeatherResponse) {
        if (openWeatherResponse == null) {
            return null;
        }

        return WeatherResponse.builder()
                .current(WeatherResponse.CurrentWeather.builder()
                        .temperature((int) (openWeatherResponse.getMain().getTemp() - 273.15)) // Convert Kelvin to Celsius
                        .windSpeed((int) openWeatherResponse.getWind().getSpeed())
                        .build())
                .build();
    }
}
