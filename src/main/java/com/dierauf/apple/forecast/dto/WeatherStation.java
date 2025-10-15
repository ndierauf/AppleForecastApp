package com.dierauf.apple.forecast.dto;

public record WeatherStation(
        Properties properties
) {

    public record Properties(
            String forecast
    ) {
    }

}
