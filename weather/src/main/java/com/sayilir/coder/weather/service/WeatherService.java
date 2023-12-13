package com.sayilir.coder.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sayilir.coder.weather.dto.WeatherDto;
import com.sayilir.coder.weather.dto.WeatherResponse;
import com.sayilir.coder.weather.model.WeatherEntity;
import com.sayilir.coder.weather.repositroy.WeatherRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class WeatherService {
    //http://api.weatherstack.com/current?accessket=123123123&query=london
    //http://api.weatherstack.com/current
    //    ? access_key = YOUR_ACCESS_KEY
    //    & query = New York
    private static final String BASE_URL = "http://api.weatherstack.com/current";
    // private static final String API_URL = "http://api.weatherstack.com/current?accessket=123123123&query=";
    private static final String API_KEY = "2e9c793c377ac42244b91bb5e86d34a4";
    private static final String API_URL = BASE_URL + "?access_key=" + API_KEY + "&query=";
    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();//jsoni nesneye cevirmek icin kullanilir

    public WeatherService(WeatherRepository weatherRepository, RestTemplate restTemplate) {
        this.weatherRepository = weatherRepository;
        this.restTemplate = restTemplate;
    }

    public WeatherDto getWeatherByCityName(String city) {
        Optional<WeatherEntity> weatherEntityOptional = weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(city);

        /* //BEFORE REFACTOR
        if (!weatherEntityOptional.isPresent()) {
            return WeatherDto.convert(getWeatherFromWeatherStack(city));
        }
        if(weatherEntityOptional.get().getUpdatedTime().isBefore(LocalDateTime.now().minusMinutes(30))){
            return WeatherDto.convert(getWeatherFromWeatherStack(city));
        }
        return WeatherDto.convert(weatherEntityOptional.get());

         */

        //AFTER REFACTOR
        return weatherEntityOptional.map(weather -> {
            if (weather.getUpdatedTime().isBefore(LocalDateTime.now().minusMinutes(30))) {
                return WeatherDto.convert(getWeatherFromWeatherStack(city));
            }
            return WeatherDto.convert(weatherEntityOptional.get());
        }).orElseGet(() -> WeatherDto.convert(getWeatherFromWeatherStack(city)));
    }

    private WeatherEntity getWeatherFromWeatherStack(String city) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(API_URL + city, String.class);

        // response json ulasmak icin -> responseEntity.getBody()
        //WeatherResponse -> WeatherEntity cevirecegiz
        try {
            WeatherResponse weatherResponse = objectMapper.readValue(responseEntity.getBody(), WeatherResponse.class);
            return saveWeatherEntity(city, weatherResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private WeatherEntity saveWeatherEntity(String city, WeatherResponse weatherResponse) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        WeatherEntity weatherEntity = new WeatherEntity(city,
                weatherResponse.location().name(),
                weatherResponse.location().country(),
                weatherResponse.current().temperature(),
                LocalDateTime.now(),
                LocalDateTime.parse(weatherResponse.location().localTime(), dateTimeFormatter)
        );
        return weatherRepository.save(weatherEntity);
    }
}
