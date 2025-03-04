package com.melbourne.weather.provider;

import com.melbourne.weather.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherStackProviderTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherStackProvider provider;

    @BeforeEach
    public void setup() {
        provider = new WeatherStackProvider(restTemplate);
    }

    @Test
    void testGetWeather_SuccessfulResponse() {
        WeatherResponse mockResponse = WeatherResponse.builder()
                .current(WeatherResponse.CurrentWeather.builder()
                        .temperature(25)
                        .windSpeed(10)
                        .build())
                .build();

        when(restTemplate.getForObject(anyString(), any())).thenReturn(mockResponse);
        WeatherResponse response = provider.getWeather();
        assertNotNull(response);
    }

    @Test
    void testGetWeather_Failure_ThrowsException() {
        when(restTemplate.getForObject(any(), any()))
                .thenThrow(new RuntimeException("API call failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> provider.getWeather());
        assertTrue(exception.getMessage().contains("Weather stack provider unavailable"));
    }

}