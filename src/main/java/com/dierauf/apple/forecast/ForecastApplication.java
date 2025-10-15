package com.dierauf.apple.forecast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for the Apple Forecast Application.
 * This application provides weather forecasts based on zip codes.
 * Caching is enabled to store recent forecasts and reduce external API calls.
 */

@SpringBootApplication(scanBasePackages = "com.dierauf.apple.forecast")
@EnableCaching // Allows for caching of weather forecasts based on zipcode. Using Caffeine as the cache provider.
// Use minimum access level necessary. Package-private for now. Reduces surface area for attack.
public class ForecastApplication {

    public static void main(String[] args) {
        System.out.println("Welcome to the Apple Weather Forecast Application!");
        SpringApplication.run(ForecastApplication.class, args);
    }

}
