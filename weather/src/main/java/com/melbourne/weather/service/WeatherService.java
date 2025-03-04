package com.melbourne.weather.service;

import com.melbourne.weather.exception.WeatherServiceException;
import com.melbourne.weather.model.WeatherResponse;
import com.melbourne.weather.provider.OpenWeatherMapProvider;
import com.melbourne.weather.provider.WeatherProvider;
import com.melbourne.weather.provider.WeatherStackProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final WeatherProvider weatherStackProvider;
    private final WeatherProvider openWeatherMapProvider;
    private final CacheManager cacheManager;

    @Autowired
    public WeatherService(WeatherStackProvider weatherStackProvider, OpenWeatherMapProvider openWeatherMapProvider,
                          CacheManager cacheManager) {
        this.weatherStackProvider = weatherStackProvider;
        this.openWeatherMapProvider = openWeatherMapProvider;
        this.cacheManager = cacheManager;
    }

    @Cacheable(value = "weatherCache", unless = "#result == null")
    public WeatherResponse getWeather() {
            WeatherResponse response = fetchWeatherFromProviders();
            if (response != null) {
                return response;
            }

        WeatherResponse staleResponse = getStaleResponse();
        if (staleResponse != null) {
            return staleResponse;
        }

        throw new WeatherServiceException("Weather data is unavailable at the moment. Please try again later.");
    }

    private WeatherResponse fetchWeatherFromProviders() {
        WeatherResponse response = null;
        try {
            response = weatherStackProvider.getWeather();
        } catch (Exception e) {
            logger.error("Primary provider failed, trying Secondary provider.", e);
        }

        if (response == null) {
            try {
                response = openWeatherMapProvider.getWeather();
            } catch (Exception e) {
                logger.error("Secondary provider failed.", e);
            }
        }
        if (response != null) {
            Cache cache = cacheManager.getCache("weatherCache");
            if (cache != null) {
                cache.put("weatherCache", response);
                logger.info("Weather data cached successfully.");
            }
        }
        return response;
    }

    private WeatherResponse getStaleResponse() {
        logger.info("Fetching data from cache");
        Cache cache = cacheManager.getCache("weatherCache");
        if (cache != null) {
            return cache.get("weatherCache", WeatherResponse.class);
        }
        return null;
    }
}
