import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WeatherApp extends Application {
    private WeatherController controller;

    public WeatherApp() {
    }

    public void start(Stage stage) {
        WeatherModel model = new WeatherModel();
        controller = new WeatherController(model);

        StackPane app = new StackPane();
        Scene scene = new Scene(app, 1600.0F, 900.0F);

        app.getChildren().setAll(new Node[]{controller.getView().daySearchScreen(app)});

        stage.setTitle("Weather App");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
