package com.dierauf.apple.forecast.dto;

public record CacheableWeatherForecast(String locationName, WeatherForecast weatherForecast, boolean isFromCache) {
}
