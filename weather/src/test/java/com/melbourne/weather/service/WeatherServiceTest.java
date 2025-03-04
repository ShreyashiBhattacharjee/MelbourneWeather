package com.melbourne.weather.service;

import com.melbourne.weather.exception.WeatherServiceException;
import com.melbourne.weather.model.WeatherResponse;
import com.melbourne.weather.provider.OpenWeatherMapProvider;
import com.melbourne.weather.provider.WeatherStackProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private WeatherStackProvider provider1;

    @Mock
    private OpenWeatherMapProvider provider2;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService(provider1, provider2, cacheManager);
    }

    @Test
    void testGetWeather_SuccessFromFirstProvider() {
        when(provider1.getWeather()).thenReturn(WeatherResponse.builder()
                .current(WeatherResponse.CurrentWeather.builder()
                        .temperature(25)
                        .windSpeed(15)
                        .build())
                .build());
        WeatherResponse response = weatherService.getWeather();
        assertEquals(25, response.getCurrent().getTemperature());
        assertEquals(15.0, response.getCurrent().getWindSpeed());
    }

    @Test
    void testFetchWeatherFromProviders_PrimaryFails_SecondarySucceeds() {
        when(provider1.getWeather()).thenThrow(new RuntimeException("Primary provider failed"));
        when(cacheManager.getCache("weatherCache")).thenReturn(cache);

        WeatherResponse mockResponse = WeatherResponse.builder()
                .current(WeatherResponse.CurrentWeather.builder()
                        .temperature(25)
                        .windSpeed(10)
                        .build())
                .build();
        when(provider2.getWeather()).thenReturn(mockResponse);
        WeatherResponse response = weatherService.getWeather();

        verify(cache).put("weatherCache", mockResponse);
        assertNotNull(response);
        assertEquals(25, response.getCurrent().getTemperature());
    }

    @Test
    void testGetWeather_SuccessFromSecondProvider() {
        when(provider1.getWeather()).thenReturn(null);
        when(provider2.getWeather()).thenReturn(WeatherResponse.builder()
                .current(WeatherResponse.CurrentWeather.builder()
                        .temperature(25)
                        .windSpeed(15)
                        .build())
                .build());
        WeatherResponse response = weatherService.getWeather();
        assertEquals(25.0, response.getCurrent().getTemperature());
        assertEquals(15.0, response.getCurrent().getWindSpeed());
    }

    @Test
    void testFetchWeatherFromProviders_BothProvidersFail_ReturnsNull() {
        when(provider1.getWeather()).thenThrow(new RuntimeException("Primary provider failed"));
        when(provider2.getWeather()).thenThrow(new RuntimeException("Secondary provider failed"));

        verify(cache, never()).put(any(), any());
        WeatherServiceException ex = assertThrows(WeatherServiceException.class, () -> {
            weatherService.getWeather();
        });
        assertEquals("Weather data is unavailable at the moment. Please try again later.", ex.getMessage());
    }

    @Test
    void testGetWeather_AllProvidersFail() {
        when(provider1.getWeather()).thenReturn(null);
        when(provider2.getWeather()).thenReturn(null);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        WeatherServiceException ex = assertThrows(WeatherServiceException.class, () -> {
            weatherService.getWeather();
        });
        assertEquals("Weather data is unavailable at the moment. Please try again later.", ex.getMessage());
    }
}