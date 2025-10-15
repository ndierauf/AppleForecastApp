package com.dierauf.apple.forecast;

import com.dierauf.apple.forecast.dto.CacheableWeatherForecast;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for retrieving weather forecast requests.
 */
@RestController
@AllArgsConstructor // Reduce boilerplate "noise" for constructor injection.
class ForecastController {

    private final ForecastService forecastService;

    @Description("""
            The endpoint returns a weather forecast based on the given address.
            Example: "123 Main St, Springfield, IL 62704".
            Returns a JSON object containing the weather forecast and a flag indicating if the data was retrieved from cache.
            """)
    @GetMapping("/forecast")
    ResponseEntity<CacheableWeatherForecast> getForecast(@RequestParam(name = "address") String address) {
        validate(address);
        CacheableWeatherForecast forecast = forecastService.getForecast(address);
        return ResponseEntity.ok(forecast);
    }

    private void validate(String address) {
        // Just basic validation. OpenStreetMap service does a good job of handling a variety of address formats.
        if (StringUtils.isBlank(address)) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
    }

}
