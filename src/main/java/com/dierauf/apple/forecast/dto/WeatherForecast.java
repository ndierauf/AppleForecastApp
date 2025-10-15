package com.dierauf.apple.forecast.dto;

import java.util.List;

public record WeatherForecast(
        Properties properties
) {

    public record Properties(
            String updateTime,
            List<Period> periods
    ) {
        public record Period(
                int number,
                String name,
                String startTime,
                String endTime,
                int temperature,
                String temperatureUnit,
                String icon,
                String detailedForecast
        ) {
        }
    }

}
