package com.sayilir.coder.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherResponse(

        Request request,
        Location location,
        Current current


) {
}
