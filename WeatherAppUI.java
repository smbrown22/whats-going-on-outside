import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WeatherAppUI extends Application {

    private final double BASE_W = 1600;
    private final double BASE_H = 900;

    private String currentCity = "Raleigh";

    // Change this manually for now:
    // "sunny", "rain", or "snow"
    private String weatherType = "sunny";

    @Override
    public void start(Stage stage) {
        StackPane app = new StackPane();
        Scene scene = new Scene(app, BASE_W, BASE_H);

        app.getChildren().setAll(daySearchScreen(app));

        stage.setTitle("Weather App");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private Pane scaledPage(StackPane app) {
        Pane page = new Pane();
        page.setPrefSize(BASE_W, BASE_H);
        page.scaleXProperty().bind(app.widthProperty().divide(BASE_W));
        page.scaleYProperty().bind(app.heightProperty().divide(BASE_H));
        return page;
    }

    private Background skyBackground() {
        if (weatherType.equalsIgnoreCase("rain")) {
            return gradient("#1f4f73", "#4e8fb7");
        }

        if (weatherType.equalsIgnoreCase("snow")) {
            return gradient("#9fc9e8", "#d9efff");
        }

        return gradient("#2d94df", "#67b8f1");
    }

    private Background gradient(String top, String bottom) {
        return new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web(top)),
                        new Stop(1, Color.web(bottom))),
                null, null
        ));
    }

    private Pane daySearchScreen(StackPane app) {
        Pane root = scaledPage(app);
        root.setBackground(gradient("#2d94df", "#67b8f1"));

        Pane sun = createSun(1425, 150, 150);
        pulse(sun);

        Label title = titleLabel();
        title.setLayoutX(380);
        title.setLayoutY(285);

        TextField search = searchBar();
        search.setLayoutX(450);
        search.setLayoutY(405);

        search.setOnAction(e -> {
            if (!search.getText().trim().isEmpty()) {
                currentCity = search.getText().trim();
            }

            app.getChildren().setAll(weatherScreen(app));
        });

        Pane cloud1 = createCloud(-80, 620, 1.08, Color.WHITE);
        Pane cloud2 = createCloud(1010, 570, 1.2, Color.WHITE);

        drift(cloud1, -80, -35);
        drift(cloud2, 1010, 1060);

        root.getChildren().addAll(sun, title, search, cloud1, cloud2);
        return root;
    }

    private Pane weatherScreen(StackPane app) {
        Pane root = scaledPage(app);
        root.setBackground(skyBackground());

        Label title = titleLabel();
        title.setLayoutX(335);
        title.setLayoutY(40);

        Label city = new Label(currentCity);
        city.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 54));
        city.setTextFill(Color.WHITE);
        city.setLayoutX(610);
        city.setLayoutY(120);

        Label temp = new Label("70°");
        temp.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 165));
        temp.setTextFill(Color.WHITE);
        temp.setLayoutX(85);
        temp.setLayoutY(335);

        Polygon arrow = new Polygon(
                0, 60, 45, 15, 90, 60,
                70, 60, 70, 150, 25, 150, 25, 60
        );
        arrow.setFill(Color.web("#f64b4b"));
        arrow.setLayoutX(465);
        arrow.setLayoutY(350);
        bounce(arrow);

        LineChart<String, Number> chart = createRealChart();
        chart.setLayoutX(595);
        chart.setLayoutY(175);

        HBox nav = new HBox(220);
        nav.setAlignment(Pos.CENTER);
        nav.setPrefWidth(BASE_W);
        nav.setLayoutY(770);

        Label home = navLabel("home");
        Label about = navLabel("about");

        makeClickable(home);
        makeClickable(about);

        home.setOnMouseClicked(e -> app.getChildren().setAll(daySearchScreen(app)));
        about.setOnMouseClicked(e -> app.getChildren().setAll(aboutScreen(app)));

        nav.getChildren().addAll(home, about);

        Pane cloud1 = createCloud(-80, 645, 1.08, Color.WHITE);
        Pane cloud2 = createCloud(1200, 610, 1.25, Color.WHITE);

        drift(cloud1, -80, -35);
        drift(cloud2, 1200, 1260);

        root.getChildren().addAll(title, city, temp, arrow, chart, cloud1, cloud2, nav);

        if (weatherType.equalsIgnoreCase("rain")) {
            root.getChildren().add(createRain());
        }

        if (weatherType.equalsIgnoreCase("snow")) {
            root.getChildren().add(createSnow());
        }

        return root;
    }

    private LineChart<String, Number> createRealChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(55, 85, 5);

        xAxis.setTickLabelFill(Color.WHITE);
        yAxis.setTickLabelFill(Color.WHITE);
        xAxis.setTickLabelFont(Font.font("Arial", FontWeight.BOLD, 15));
        yAxis.setTickLabelFont(Font.font("Arial", FontWeight.BOLD, 15));

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setPrefSize(950, 550);
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        chart.setCreateSymbols(true);

        chart.setStyle("""
                -fx-background-color: rgba(30,95,145,0.65);
                -fx-background-radius: 28;
                -fx-padding: 20;
                """);

        XYChart.Series<String, Number> temps = new XYChart.Series<>();
        temps.getData().add(new XYChart.Data<>("Now", 70));
        temps.getData().add(new XYChart.Data<>("3 PM", 64));
        temps.getData().add(new XYChart.Data<>("6 PM", 72));
        temps.getData().add(new XYChart.Data<>("9 PM", 66));
        temps.getData().add(new XYChart.Data<>("12 AM", 80));

        chart.getData().add(temps);

        chart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                chart.lookup(".chart-series-line").setStyle("-fx-stroke: white; -fx-stroke-width: 4px;");
                chart.lookupAll(".chart-line-symbol").forEach(n ->
                        n.setStyle("-fx-background-color: white, white; -fx-background-radius: 8px; -fx-padding: 6px;")
                );
                chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
            }
        });

        return chart;
    }

    private Pane createRain() {
        Pane rain = new Pane();
        rain.setMouseTransparent(true);

        for (int i = 0; i < 90; i++) {
            Line drop = new Line(0, 0, -8, 32);
            drop.setStroke(Color.rgb(210, 235, 255, 0.75));
            drop.setStrokeWidth(2);

            drop.setLayoutX(Math.random() * BASE_W);
            drop.setLayoutY(Math.random() * BASE_H);

            TranslateTransition fall = new TranslateTransition(Duration.seconds(0.9 + Math.random()), drop);
            fall.setFromY(-80);
            fall.setToY(BASE_H + 120);
            fall.setCycleCount(Animation.INDEFINITE);
            fall.setInterpolator(Interpolator.LINEAR);
            fall.setDelay(Duration.seconds(Math.random()));
            fall.play();

            rain.getChildren().add(drop);
        }

        return rain;
    }

    private Pane createSnow() {
        Pane snow = new Pane();
        snow.setMouseTransparent(true);

        for (int i = 0; i < 80; i++) {
            Circle flake = new Circle(3 + Math.random() * 4);
            flake.setFill(Color.rgb(255, 255, 255, 0.85));

            flake.setLayoutX(Math.random() * BASE_W);
            flake.setLayoutY(Math.random() * BASE_H);

            TranslateTransition fall = new TranslateTransition(Duration.seconds(4 + Math.random() * 4), flake);
            fall.setFromY(-80);
            fall.setToY(BASE_H + 120);
            fall.setCycleCount(Animation.INDEFINITE);
            fall.setInterpolator(Interpolator.LINEAR);
            fall.setDelay(Duration.seconds(Math.random() * 3));
            fall.play();

            RotateTransition spin = new RotateTransition(Duration.seconds(3), flake);
            spin.setByAngle(360);
            spin.setCycleCount(Animation.INDEFINITE);
            spin.play();

            snow.getChildren().add(flake);
        }

        return snow;
    }

    private Pane aboutScreen(StackPane app) {
        Pane root = scaledPage(app);
        root.setBackground(gradient("#1e3c72", "#2a5298"));

        Label title = new Label("about");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 85));
        title.setTextFill(Color.WHITE);
        title.setLayoutX(675);
        title.setLayoutY(210);

        Label info = new Label("Weather App UI\nBuilt with JavaFX");
        info.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        info.setTextFill(Color.WHITE);
        info.setLayoutX(575);
        info.setLayoutY(365);

        Label home = navLabel("home");
        home.setLayoutX(710);
        home.setLayoutY(650);
        makeClickable(home);
        home.setOnMouseClicked(e -> app.getChildren().setAll(daySearchScreen(app)));

        root.getChildren().addAll(createMoon(1380, 185, 145), title, info, home);
        return root;
    }

    private Label titleLabel() {
        Label title = new Label("what's going on outside?");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 72));
        title.setTextFill(Color.WHITE);
        return title;
    }

    private TextField searchBar() {
        TextField search = new TextField();
        search.setPromptText("search...");
        search.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 46));
        search.setPrefSize(700, 88);
        search.setStyle("""
                -fx-background-color: rgba(235,245,255,0.78);
                -fx-background-radius: 12;
                -fx-prompt-text-fill: #7fa2bf;
                -fx-text-fill: #1e5b8c;
                -fx-padding: 0 20 0 20;
                """);
        return search;
    }

    private Label navLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 62));
        label.setTextFill(Color.web("#13598b"));
        return label;
    }

    private void makeClickable(Label label) {
        label.setStyle("-fx-cursor: hand;");
        label.setOnMouseEntered(e -> {
            label.setScaleX(1.08);
            label.setScaleY(1.08);
            label.setTextFill(Color.web("#083c65"));
        });
        label.setOnMouseExited(e -> {
            label.setScaleX(1);
            label.setScaleY(1);
            label.setTextFill(Color.web("#13598b"));
        });
    }

    private Pane createSun(double x, double y, double r) {
        Pane sun = new Pane();

        Circle circle = new Circle(x, y, r);
        circle.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ffc928")),
                new Stop(1, Color.web("#ffae25"))));

        sun.getChildren().add(circle);

        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            Line ray = new Line(
                    x + Math.cos(angle) * (r + 22),
                    y + Math.sin(angle) * (r + 22),
                    x + Math.cos(angle) * (r + 58),
                    y + Math.sin(angle) * (r + 58)
            );
            ray.setStroke(Color.web("#ffbd28"));
            ray.setStrokeWidth(18);
            ray.setStrokeLineCap(StrokeLineCap.ROUND);
            sun.getChildren().add(ray);
        }

        return sun;
    }

    private Pane createMoon(double x, double y, double r) {
        Pane moon = new Pane();

        Circle base = new Circle(x, y, r, Color.web("#eef2f5"));
        Circle c1 = new Circle(x - 90, y + 40, 105, Color.rgb(90, 100, 105, 0.45));
        Circle c2 = new Circle(x + 75, y - 75, 35, Color.rgb(100, 110, 115, 0.35));
        Circle c3 = new Circle(x + 100, y + 80, 45, Color.rgb(100, 110, 115, 0.35));

        moon.getChildren().addAll(base, c1, c2, c3);
        return moon;
    }

    private Pane createCloud(double x, double y, double scale, Color color) {
        Pane cloud = new Pane();
        cloud.setLayoutX(x);
        cloud.setLayoutY(y);
        cloud.setScaleX(scale);
        cloud.setScaleY(scale);

        Circle c1 = new Circle(140, 150, 110, color);
        Circle c2 = new Circle(300, 95, 165, color);
        Circle c3 = new Circle(500, 185, 145, color);
        Circle c4 = new Circle(40, 210, 100, color);

        Ellipse smoothBase = new Ellipse(315, 250, 330, 110);
        smoothBase.setFill(color);

        cloud.getChildren().addAll(c4, c1, c2, c3, smoothBase);
        return cloud;
    }

    private void pulse(Pane node) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(2.4), node);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.035);
        st.setToY(1.035);
        st.setAutoReverse(true);
        st.setCycleCount(Animation.INDEFINITE);
        st.play();
    }

    private void drift(Pane node, double fromX, double toX) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(5.5), node);
        tt.setFromX(0);
        tt.setToX(toX - fromX);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    private void bounce(Polygon node) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.4), node);
        tt.setFromY(0);
        tt.setToY(-18);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}