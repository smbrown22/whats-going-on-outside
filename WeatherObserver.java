package model;

public interface WeatherObserver {
    // this lets the model send new weather data to the ui
    void updateWeather(String weatherData);

    // this sends a heads up if something goes wrong
    void updateError(String errorMessage);
}