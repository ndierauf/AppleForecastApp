package com.dierauf.apple.forecast;

import com.dierauf.apple.forecast.dto.AddressRecord;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Service for retrieving geographical coordinates (longitude and latitude) based on a given address or location name.
 * Utilizes the Nominatim API from OpenStreetMap to perform geocoding.
 */
@Service
class LocationService {

    public static final String LOCATION_URL = "https://nominatim.openstreetmap.org";

    private final RestClient restClient;

    LocationService(RestClient.Builder clientBuilder) {
        this.restClient = clientBuilder
                .baseUrl(LOCATION_URL)
                .build();
    }

    AddressRecord retrieveLongitudeAndLatitude(String param) {
        List<AddressRecord> addressRecords = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search")
                        .queryParam("format", "jsonv2")
                        .queryParam("addressdetails", "1")
                        .queryParam("q", param)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        if (CollectionUtils.isEmpty(addressRecords)) {
            throw new IllegalStateException("No address records found for: " + param);
        }
        return addressRecords.getFirst();
    }

}
