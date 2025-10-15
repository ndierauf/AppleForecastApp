package com.dierauf.apple.forecast;

import com.dierauf.apple.forecast.dto.WeatherForecast;
import com.dierauf.apple.forecast.dto.WeatherStation;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * Service to retrieve weather forecast information from the National Weather Service, based upon a provided address.
 */
@Service
class WeatherService {

    static final String NWS_URL = "https://api.weather.gov";

    private final RestClient restClient;

    WeatherService(RestClient.Builder clientBuilder) {
        this.restClient = clientBuilder
                .baseUrl(NWS_URL)
                .build();
    }

    /**
     * Retrieves weather forecast information from the National Weather Service (NWS) based upon latitude and longitude
     * coordinates.
     *
     * @param lat Latitude coordinate as a String.
     * @param lon Longitude coordinate as a String.
     * @return WeatherForecast object containing the forecast data.
     */
    // Note, the National Weather Service (NWS) API requires two requests.
    // The first request retrieves the URL for data from the nearest NWS field office of the given coordinates.
    // The second request retrieves the actual forecast data using the previous URL.
    WeatherForecast retrieveWeatherForecast(String lat, String lon) {
        String closestFieldOfficeUrl = retrieveClosestFieldOfficeUrl(lat, lon);
        return retrieveForecastData(closestFieldOfficeUrl);
    }

    private String retrieveClosestFieldOfficeUrl(String lat, String lon) {
        WeatherStation weatherStation = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/points/%s,%s".formatted(lat, lon)) // Efficient String building.
                        .build())
                .retrieve()
                .body(WeatherStation.class);
        return Optional.ofNullable(Objects.requireNonNull(weatherStation).properties().forecast())
                .orElseThrow(() -> new IllegalStateException(
                        ("No National Weather Service station available for coordinates: %s, %s").formatted(lat, lon)));
    }

    private WeatherForecast retrieveForecastData(String closestFieldOfficeUrl) {
        String urlPath = asUrlPath(closestFieldOfficeUrl);
        WeatherForecast weatherForecast = restClient.get()
                .uri(uriBuilder -> uriBuilder.path(urlPath) // Efficient String handling.
                        .build())
                .retrieve()
                .body(WeatherForecast.class);
        return Optional.of(Objects.requireNonNull(weatherForecast))
                .orElseThrow(() -> new IllegalStateException("No forecast available from the provided NWS URL: %s"
                        .formatted(closestFieldOfficeUrl)));
    }

    // Method abstracts URL path creation and exception details from business logic.
    private String asUrlPath(String closestFieldOfficeUrl) {
        try {
            URI uri = new URI(closestFieldOfficeUrl);
            URL url = uri.toURL();
            return url.getPath();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
