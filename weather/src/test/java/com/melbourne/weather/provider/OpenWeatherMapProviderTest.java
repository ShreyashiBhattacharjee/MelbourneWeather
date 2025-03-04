package com.melbourne.weather.provider;

import com.melbourne.weather.model.OpenWeatherResponse;
import com.melbourne.weather.model.OpenWeatherResponse.Main;
import com.melbourne.weather.model.OpenWeatherResponse.Wind;
import com.melbourne.weather.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenWeatherMapProviderTest {

    @Mock
    private RestTemplate restTemplate;

    private OpenWeatherMapProvider provider;

    @BeforeEach
    void setup() {

        provider = new OpenWeatherMapProvider(restTemplate);
        ReflectionTestUtils.setField(provider, "API_URL", "https://mock-api.com/weather");
    }

    @Test
    void testGetWeather_Success() {
        OpenWeatherResponse mockResponse = mockOpenWeatherResponse();
        when(restTemplate.getForObject(anyString(), eq(OpenWeatherResponse.class))).thenReturn(mockResponse);

        WeatherResponse result = provider.getWeather();

        assertNotNull(result);
        assertEquals(19, result.getCurrent().getTemperature());
        assertEquals(6, result.getCurrent().getWindSpeed());
    }

    @Test
    void testGetWeather_Failure_ThrowsException() {
        when(restTemplate.getForObject(anyString(), any()))
                .thenThrow(new RuntimeException("API call failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> provider.getWeather());
        assertTrue(exception.getMessage().contains("OpenWeatherMap provider unavailable"));
    }

    private OpenWeatherResponse mockOpenWeatherResponse() {
        OpenWeatherResponse response = new OpenWeatherResponse();
        response.setMain(Main.builder()
                .temp(292.63)
                .build());
        response.setWind(Wind.builder()
                .speed(6.61)
                .build());
        return response;
    }
}
