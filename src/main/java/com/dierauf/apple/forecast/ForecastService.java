package com.dierauf.apple.forecast;

import com.dierauf.apple.forecast.dto.AddressRecord;
import com.dierauf.apple.forecast.dto.CacheableWeatherForecast;
import com.dierauf.apple.forecast.dto.WeatherForecast;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service to retrieve weather forecast information based upon a provided address.
 * Caches results for 30 minutes to avoid excessive calls to external services.
 * Uses two services:
 * 1. OpenStreetMap (OSM) to translate address to longitude and latitude.
 * 2. National Weather Service (NWS) to retrieve forecast based upon longitude and latitude.
 */
@Service
@AllArgsConstructor
@Slf4j
class ForecastService {

    public static final int MAX_PERIODS = 3;

    private final LocationService locationService;
    private final WeatherService weatherService;
    private final CacheManager cacheManager;

    CacheableWeatherForecast getForecast(String address) {
        AddressRecord addressRecord = retrieveLongitudeAndLatitude(address);
        return retrieveWeatherForecast(addressRecord);
    }

    private AddressRecord retrieveLongitudeAndLatitude(String address) {
        // Call for each request to obtain address details, including longitude, latitude, and zipcode.
        return locationService.retrieveLongitudeAndLatitude(address);
    }

    private CacheableWeatherForecast retrieveWeatherForecast(AddressRecord addressRecord) {
        String postcode = addressRecord.address().postcode();
        CacheableWeatherForecast fromCache = getFromCache(postcode);
        if (fromCache != null) {
            log.info("Cache hit for postcode: {}", postcode);
            return fromCache;
        }
        log.info("Cache miss for postcode: {}", postcode);
        WeatherForecast weatherForecast = weatherService.retrieveWeatherForecast(addressRecord.lat(), addressRecord.lon());
        WeatherForecast truncatedForecast = truncatedForecast(weatherForecast);
        putIntoCache(postcode, truncatedForecast, addressRecord);
        return new CacheableWeatherForecast(addressRecord.displayName(), truncatedForecast, false);
    }

    // No need to send the entire forecast data. Just send the first few periods.
    private WeatherForecast truncatedForecast(WeatherForecast weatherForecast) {
        WeatherForecast.Properties properties = weatherForecast.properties();
        if (properties.periods().size() <= MAX_PERIODS) {
            return weatherForecast;
        }
        return new WeatherForecast(
                new WeatherForecast.Properties(
                        properties.updateTime(),
                        properties.periods().subList(0, MAX_PERIODS)
                )
        );
    }

    private void putIntoCache(String postcode, WeatherForecast weatherForecast, AddressRecord addressRecord) {
        if (StringUtils.isBlank(postcode)) {
            return;
        }
        cacheManager().put(postcode, new CacheableWeatherForecast(addressRecord.displayName(), weatherForecast, true));
    }

    private CacheableWeatherForecast getFromCache(String postcode) {
        if (StringUtils.isBlank(postcode)) {
            return null;
        }
        return cacheManager().get(postcode, CacheableWeatherForecast.class);
    }

    private Cache cacheManager() {
        return Objects.requireNonNull(cacheManager.getCache("cacheManager"));
    }

}
