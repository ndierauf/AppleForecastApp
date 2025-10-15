package com.dierauf.apple.forecast;

import com.dierauf.apple.forecast.config.CacheConfig;
import com.dierauf.apple.forecast.dto.AddressRecord;
import com.dierauf.apple.forecast.dto.WeatherForecast;
import com.dierauf.apple.forecast.dto.WeatherStation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Function;

import static com.dierauf.apple.forecast.WeatherService.NWS_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for ForecastController.
 * Uses MockMvc to simulate HTTP requests and verify responses.
 * Mocks external REST client interactions to isolate controller logic.
 */
@WebMvcTest({ForecastController.class, ForecastService.class,
        LocationService.class, WeatherService.class,
        CacheConfig.class})
@Import(MockConfig.class)
class ForecastControllerTest {

    public static final String TIME = "2025-10-14T20:26:19+00:00";
    @Autowired
    RestClient.Builder clientBuilder;

    @Autowired
    private ForecastController forecastController;

    @Autowired
    private ForecastService forecastService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CacheConfig cacheConfig;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private MockMvc mockMvc;

    AddressRecord addressRecord = createAddressRecord();
    WeatherStation weatherStation = createWeatherStation();
    WeatherForecast weatherForecast = createWeatherForecast();


    /**
     * This test exercises the entire flow of retrieving a weather forecast for a given location.
     */
    @Test
    void retrieveWeatherForecastForLocation() throws Exception {
        mockRestClientBuilderForLocation();
        mockMvc.perform(get("/forecast").param("address", addressRecord.displayName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFromCache").value(false))
                .andExpect(jsonPath("$.locationName").value(addressRecord.displayName()))
                .andExpect(jsonPath("$.weatherForecast.properties.updateTime").value(TIME))
                .andExpect(jsonPath("$.weatherForecast.properties.periods[0].name").value("Tonight"));

        // Second call should come from cache.
        mockMvc.perform(get("/forecast").param("address", addressRecord.displayName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isFromCache").value(true)); // Checks out ok.
    }

    private void mockRestClientBuilderForLocation() throws URISyntaxException {
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(clientBuilder.build().get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(List.of(addressRecord)); // For LocationService.
        when(responseSpec.body(WeatherForecast.class)).thenReturn(weatherForecast); // For WeatherService.
        when(responseSpec.body(WeatherStation.class)).thenReturn(weatherStation); // For WeatherService.
    }

    WeatherForecast createWeatherForecast() {
        return new WeatherForecast(new WeatherForecast.Properties(TIME, List.of(
                getPeriod(1, "Tonight"),
                getPeriod(2, "Tomorrow"),
                getPeriod(1, "Tomorrow Night")
        )));
    }

    private WeatherForecast.Properties.Period getPeriod(int number, String when) {
        return new WeatherForecast.Properties.Period(number, when, TIME,
                TIME, 48, "F",
                "https://api.weather.gov/icons/land/day/rain_showers,50?size=medium",
                "A chance of rain showers. Mostly cloudy. High near 62, "
                        + "with temperatures falling to around 59 in the afternoon. "
                        + "West northwest wind around 8 mph. Chance of precipitation is 50%. "
                        + "New rainfall amounts less than a tenth of an inch possible.");
    }

    WeatherStation createWeatherStation() {
        return new WeatherStation(new WeatherStation.Properties(NWS_URL + "/gridpoints/ILX/63,39/forecast"));
    }

    private AddressRecord createAddressRecord() {
        return new AddressRecord(
                "111",
                "222",
                "123 Main St, Springfield, IL 62704",
                new AddressRecord.Address(
                        "123",
                        "Main St",
                        null,
                        "Springfield",
                        "Sangamon",
                        "IL",
                        "62704",
                        "US"
                )
        );
    }

}
