package com.sayilir.coder.weather.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sayilir.coder.weather.model.WeatherEntity;

import java.time.LocalDateTime;

public record WeatherDto(
        String cityName,
        String country,
        Integer temperature,
        @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime updatedTime
) {
   public static WeatherDto convert(WeatherEntity from) {
        return new WeatherDto(
                from.getCityName(),
                from.getCountry(),
                from.getTemperature(),
                from.getUpdatedTime());
    }
}
