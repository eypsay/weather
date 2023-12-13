package com.sayilir.coder.weather.repositroy;

import com.sayilir.coder.weather.model.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherEntity, String> {
    // select * from entity where requestedCityName order by updateTime desc limit 1
    Optional<WeatherEntity> findFirstByRequestedCityNameOrderByUpdatedTimeDesc(String city);
}
