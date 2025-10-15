# Forecast Application

## Overview
A web application for retreiving forecast data for a given street address.

## Features
- This is a simple web application to demonstrate basic knowledge of the Java programming language, using Spring Boot.
- The application uses REST requests to open-source services to retrieve location and weather information.
- It has a very simple UI that contains a form for inputting a street address. When the Submit button is clicked, a call is made to the backend to retrieve an accurate weather forecast for that address, and updates the UI with this information.
- It uses a simple Caffeine cache to cache weather results for 30 minutes, based on the zipcode of the address.
- As I am primarily a backend software developer, most of my efforts were spent in that space. I did use Google when needed, especially in getting help with the frontend GUI. However, I believe that the application accurately showcases my knowledge and creativity in designing an enterprise type web application.
- I have placed comments to highlight decisions that I have made with regards to coding practices.

## Requirements
- Java 25
- Maven 3.x

## Setup

Clone the repository:
```bash
git clone https://github.com/ndierauf/AppleForecastApp.git
cd AppleForecastApp
mvn clean install
mvn spring-boot:run
```
Navigate your browser to: http://localhost:8080/
