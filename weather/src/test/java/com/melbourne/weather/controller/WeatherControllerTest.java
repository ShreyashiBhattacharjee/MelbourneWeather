package com.melbourne.weather.controller;

import com.melbourne.weather.model.WeatherResponse;
import com.melbourne.weather.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    void setUp() {
        weatherController = new WeatherController(weatherService);
    }

    @Test
    public void testGetWeather() {
        Mockito.when(weatherService.getWeather()).thenReturn(WeatherResponse.builder()
                .current(WeatherResponse.CurrentWeather.builder()
                        .temperature(25)
                        .windSpeed(15)
                        .build())
                .build());

        ResponseEntity<WeatherResponse> res = weatherController.getWeather();
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
        Assertions.assertEquals(25, res.getBody().getCurrent().getTemperature());
        Assertions.assertEquals(15, res.getBody().getCurrent().getWindSpeed());
    }
}
