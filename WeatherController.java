import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class WeatherController {
    private final WeatherModel model;
    private final WeatherView view;

    private String currentCity = "Raleigh";
    private String weatherType = "sunny";
    private WeatherData weatherData;

    public WeatherController(WeatherModel model) {
        this.model = model;
        this.view = new WeatherView(this);
    }

    public WeatherView getView() {
        return view;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }

    public void showSearchScreen(StackPane app) {
        app.getChildren().setAll(new Node[]{view.daySearchScreen(app)});
    }

    public void showWeatherScreen(StackPane app) {
        app.getChildren().setAll(new Node[]{view.weatherScreen(app)});
    }

    public void showAboutScreen(StackPane app) {
        app.getChildren().setAll(new Node[]{view.aboutScreen(app)});
    }

    public void showErrorScreen(StackPane app, String message) {
        app.getChildren().setAll(new Node[]{view.errorScreen(app, message)});
    }

    public void searchCity(StackPane app, String typedCity) {
        typedCity = typedCity.trim();

        if (typedCity.isEmpty()) {
            showErrorScreen(app, "Please enter a city.");
        } else {
            try {
                weatherData = model.fetchWeatherData(typedCity);
                currentCity = weatherData.locationName;
                weatherType = model.getWeatherType(weatherData.conditionText);
                showWeatherScreen(app);
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorScreen(app, "Error: " + ex.getMessage());
            }
        }
    }
}
