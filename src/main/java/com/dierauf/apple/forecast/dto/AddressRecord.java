package com.dierauf.apple.forecast.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressRecord(
        String lat,
        String lon,
        @JsonProperty("display_name")
        String displayName,
        Address address
) {
    public record Address(
            String house_number,
            String road,
            String town,
            String county,
            String state,
            String postcode,
            String country,
            String country_code
    ) {
    }

}
