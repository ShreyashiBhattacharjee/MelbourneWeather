package com.melbourne.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse implements Serializable {

    @JsonProperty("current")
    private CurrentWeather current;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentWeather {

        @JsonProperty("temperature")
        private int temperature;

        @JsonProperty("wind_speed")
        private int windSpeed;

    }
}
