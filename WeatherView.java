import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class WeatherView {
    private final double BASE_W = 1600.0F;
    private final double BASE_H = 900.0F;
    private final WeatherController controller;

    public WeatherView(WeatherController controller) {
        this.controller = controller;
    }

    public Pane scaledPage(StackPane app) {
        Pane page = new Pane();
        page.setPrefSize(BASE_W, BASE_H);
        page.scaleXProperty().bind(app.widthProperty().divide(BASE_W));
        page.scaleYProperty().bind(app.heightProperty().divide(BASE_H));
        return page;
    }

    public Background skyBackground() {
        WeatherData weatherData = controller.getWeatherData();
        String weatherType = controller.getWeatherType();

        if (weatherType.equalsIgnoreCase("rain")) {
            return gradient("#1f4f73", "#4e8fb7");
        } else if (weatherType.equalsIgnoreCase("snow")) {
            return gradient("#9fc9e8", "#d9efff");
        } else {
            return weatherData != null && !weatherData.isDay ? gradient("#061629", "#12365c") : gradient("#2d94df", "#67b8f1");
        }
    }

    public Background gradient(String top, String bottom) {
        return new Background(new BackgroundFill[]{new BackgroundFill(new LinearGradient(0.0F, 0.0F, 0.0F, 1.0F, true, CycleMethod.NO_CYCLE, new Stop[]{new Stop(0.0F, Color.web(top)), new Stop(1.0F, Color.web(bottom))}), null, null)});
    }

    public Pane daySearchScreen(StackPane app) {
        Pane root = scaledPage(app);
        root.setBackground(gradient("#2d94df", "#67b8f1"));

        Pane sun = createSun(1425.0F, 150.0F, 150.0F);
        pulse(sun);

        Label title = titleLabel();
        title.setLayoutX(380.0F);
        title.setLayoutY(285.0F);

        TextField search = searchBar();
        search.setLayoutX(450.0F);
        search.setLayoutY(405.0F);
        search.setOnAction((e) -> controller.searchCity(app, search.getText()));

        Pane cloud1 = createCloud(-80.0F, 620.0F, 1.08, Color.WHITE);
        Pane cloud2 = createCloud(1010.0F, 570.0F, 1.2, Color.WHITE);
        drift(cloud1, -80.0F, -35.0F);
        drift(cloud2, 1010.0F, 1060.0F);

        root.getChildren().addAll(new Node[]{sun, title, search, cloud1, cloud2});
        return root;
    }

    public Pane weatherScreen(StackPane app) {
        WeatherData weatherData = controller.getWeatherData();
        String weatherType = controller.getWeatherType();

        Pane root = scaledPage(app);
        root.setBackground(skyBackground());

        Label title = titleLabel();
        title.setLayoutX(335.0F);
        title.setLayoutY(40.0F);

        Label city = new Label(controller.getCurrentCity());
        city.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 54.0F));
        city.setTextFill(Color.WHITE);
        city.setLayoutX(610.0F);
        city.setLayoutY(120.0F);

        Label temp = new Label(Math.round(weatherData.currentTemp) + "°");
        temp.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 165.0F));
        temp.setTextFill(Color.WHITE);
        temp.setLayoutX(85.0F);
        temp.setLayoutY(335.0F);

        Label condition = new Label(weatherData.conditionText);
        condition.setFont(Font.font("Arial", FontWeight.BOLD, 38.0F));
        condition.setTextFill(Color.WHITE);
        condition.setLayoutX(100.0F);
        condition.setLayoutY(525.0F);

        Label updated = new Label("Updated: " + weatherData.lastUpdated);
        updated.setFont(Font.font("Arial", FontWeight.BOLD, 24.0F));
        updated.setTextFill(Color.WHITE);
        updated.setLayoutX(105.0F);
        updated.setLayoutY(575.0F);

        Polygon arrow = new Polygon(new double[]{0.0F, 60.0F, 45.0F, 15.0F, 90.0F, 60.0F, 70.0F, 60.0F, 70.0F, 150.0F, 25.0F, 150.0F, 25.0F, 60.0F});
        if (weatherData.tempChange >= 0.0F) {
            arrow.setFill(Color.web("#f64b4b"));
        } else {
            arrow.setFill(Color.web("#4bc3ff"));
            arrow.setRotate(180.0F);
        }

        arrow.setLayoutX(465.0F);
        arrow.setLayoutY(350.0F);
        bounce(arrow);

        LineChart<String, Number> chart = createRealChart();
        chart.setLayoutX(595.0F);
        chart.setLayoutY(175.0F);

        HBox nav = new HBox(220.0F);
        nav.setAlignment(Pos.CENTER);
        nav.setPrefWidth(1600.0F);
        nav.setLayoutY(770.0F);

        Label home = navLabel("home");
        Label about = navLabel("about");
        makeClickable(home);
        makeClickable(about);
        home.setOnMouseClicked((e) -> controller.showSearchScreen(app));
        about.setOnMouseClicked((e) -> controller.showAboutScreen(app));
        nav.getChildren().addAll(new Node[]{home, about});

        Color cloudColor = weatherType.equalsIgnoreCase("rain") ? Color.rgb(220, 230, 240, 0.88) : Color.WHITE;
        Pane cloud1 = createCloud(-80.0F, 645.0F, 1.08, cloudColor);
        Pane cloud2 = createCloud(1200.0F, 610.0F, 1.25F, cloudColor);
        drift(cloud1, -80.0F, -35.0F);
        drift(cloud2, 1200.0F, 1260.0F);

        root.getChildren().addAll(new Node[]{title, city, temp, condition, updated, arrow, chart, cloud1, cloud2, nav});

        if (weatherData != null && !weatherData.isDay) {
            root.getChildren().add(createStars());
            root.getChildren().add(createMoon(1380.0F, 185.0F, 145.0F));
        }

        if (weatherType.equalsIgnoreCase("rain")) {
            root.getChildren().add(createRain());
        }

        if (weatherType.equalsIgnoreCase("snow")) {
            root.getChildren().add(createSnow());
        }

        return root;
    }

    public LineChart<String, Number> createRealChart() {
        WeatherData weatherData = controller.getWeatherData();

        CategoryAxis xAxis = new CategoryAxis();
        double min = weatherData.minChartTemp - 5.0F;
        double max = weatherData.maxChartTemp + 5.0F;

        if (min == max) {
            min -= 5.0F;
            max += 5.0F;
        }

        NumberAxis yAxis = new NumberAxis(min, max, 5.0F);
        xAxis.setTickLabelFill(Color.WHITE);
        yAxis.setTickLabelFill(Color.WHITE);
        xAxis.setTickLabelFont(Font.font("Arial", FontWeight.BOLD, 15.0F));
        yAxis.setTickLabelFont(Font.font("Arial", FontWeight.BOLD, 15.0F));

        LineChart<String, Number> chart = new LineChart(xAxis, yAxis);
        chart.setPrefSize(950.0F, 550.0F);
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        chart.setCreateSymbols(true);
        chart.setStyle("-fx-background-color: rgba(30,95,145,0.65);\n-fx-background-radius: 28;\n-fx-padding: 20;\n");

        XYChart.Series<String, Number> temps = new XYChart.Series();

        for (int i = 0; i < weatherData.hourLabels.length; ++i) {
            temps.getData().add(new XYChart.Data(weatherData.hourLabels[i], weatherData.hourTemps[i]));
        }

        chart.getData().add(temps);
        chart.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                if (chart.lookup(".chart-series-line") != null) {
                    chart.lookup(".chart-series-line").setStyle("-fx-stroke: white; -fx-stroke-width: 4px;");
                }

                chart.lookupAll(".chart-line-symbol").forEach((n) -> n.setStyle("-fx-background-color: white, white; -fx-background-radius: 8px; -fx-padding: 6px;"));

                if (chart.lookup(".chart-plot-background") != null) {
                    chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
                }
            }
        });

        return chart;
    }

    public Pane createRain() {
        Pane rain = new Pane();
        rain.setMouseTransparent(true);

        for (int i = 0; i < 90; ++i) {
            Line drop = new Line(0.0F, 0.0F, -8.0F, 32.0F);
            drop.setStroke(Color.rgb(210, 235, 255, 0.75F));
            drop.setStrokeWidth(2.0F);
            drop.setLayoutX(Math.random() * 1600.0F);
            drop.setLayoutY(Math.random() * 900.0F);

            TranslateTransition fall = new TranslateTransition(Duration.seconds(0.9 + Math.random()), drop);
            fall.setFromY(-80.0F);
            fall.setToY(1020.0F);
            fall.setCycleCount(-1);
            fall.setInterpolator(Interpolator.LINEAR);
            fall.setDelay(Duration.seconds(Math.random()));
            fall.play();

            rain.getChildren().add(drop);
        }

        return rain;
    }

    public Pane createSnow() {
        Pane snow = new Pane();
        snow.setMouseTransparent(true);

        for (int i = 0; i < 80; ++i) {
            Circle flake = new Circle(3.0F + Math.random() * 4.0F);
            flake.setFill(Color.rgb(255, 255, 255, 0.85));
            flake.setLayoutX(Math.random() * 1600.0F);
            flake.setLayoutY(Math.random() * 900.0F);

            TranslateTransition fall = new TranslateTransition(Duration.seconds(4.0F + Math.random() * 4.0F), flake);
            fall.setFromY(-80.0F);
            fall.setToY(1020.0F);
            fall.setCycleCount(-1);
            fall.setInterpolator(Interpolator.LINEAR);
            fall.setDelay(Duration.seconds(Math.random() * 3.0F));
            fall.play();

            RotateTransition spin = new RotateTransition(Duration.seconds(3.0F), flake);
            spin.setByAngle(360.0F);
            spin.setCycleCount(-1);
            spin.play();

            snow.getChildren().add(flake);
        }

        return snow;
    }

    public Pane createStars() {
        Pane stars = new Pane();
        stars.setMouseTransparent(true);

        for (int i = 0; i < 70; ++i) {
            Circle star = new Circle(1.5F + Math.random() * 2.8);
            star.setFill(Color.rgb(255, 255, 255, 0.8));
            star.setLayoutX(Math.random() * 1600.0F);
            star.setLayoutY(Math.random() * 520.0F);

            FadeTransition twinkle = new FadeTransition(Duration.seconds(1.2 + Math.random() * 1.8), star);
            twinkle.setFromValue(0.3);
            twinkle.setToValue(1.0F);
            twinkle.setAutoReverse(true);
            twinkle.setCycleCount(-1);
            twinkle.setDelay(Duration.seconds(Math.random() * 2.0F));
            twinkle.play();

            stars.getChildren().add(star);
        }

        return stars;
    }

    public Pane aboutScreen(StackPane app) {
        Pane root = scaledPage(app);
        root.setBackground(gradient("#1e3c72", "#2a5298"));

        Label title = new Label("about");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 85.0F));
        title.setTextFill(Color.WHITE);
        title.setLayoutX(675.0F);
        title.setLayoutY(210.0F);

        Label info = new Label("Weather App UI\nBuilt with JavaFX\nPowered by WeatherAPI\nElizabeth and Simone");
        info.setFont(Font.font("Arial", FontWeight.BOLD, 42.0F));
        info.setTextFill(Color.WHITE);
        info.setLayoutX(575.0F);
        info.setLayoutY(365.0F);

        Label home = navLabel("home");
        home.setLayoutX(710.0F);
        home.setLayoutY(650.0F);
        makeClickable(home);
        home.setOnMouseClicked((e) -> controller.showSearchScreen(app));

        root.getChildren().addAll(new Node[]{createMoon(1380.0F, 185.0F, 145.0F), title, info, home});
        return root;
    }

    public Pane errorScreen(StackPane app, String message) {
        Pane root = scaledPage(app);
        root.setBackground(gradient("#1e3c72", "#2a5298"));

        Label title = new Label("oops!");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 95.0F));
        title.setTextFill(Color.WHITE);
        title.setLayoutX(660.0F);
        title.setLayoutY(230.0F);

        Label info = new Label(message);
        info.setFont(Font.font("Arial", FontWeight.BOLD, 34.0F));
        info.setTextFill(Color.WHITE);
        info.setLayoutX(385.0F);
        info.setLayoutY(390.0F);

        Label home = navLabel("home");
        home.setLayoutX(710.0F);
        home.setLayoutY(650.0F);
        makeClickable(home);
        home.setOnMouseClicked((e) -> controller.showSearchScreen(app));

        root.getChildren().addAll(new Node[]{createMoon(1380.0F, 185.0F, 145.0F), title, info, home});
        return root;
    }

    public Label titleLabel() {
        Label title = new Label("what's going on outside?");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 72.0F));
        title.setTextFill(Color.WHITE);
        return title;
    }

    public TextField searchBar() {
        TextField search = new TextField();
        search.setPromptText("search...");
        search.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 46.0F));
        search.setPrefSize(700.0F, 88.0F);
        search.setStyle("-fx-background-color: rgba(235,245,255,0.78);\n-fx-background-radius: 12;\n-fx-prompt-text-fill: #7fa2bf;\n-fx-text-fill: #1e5b8c;\n-fx-padding: 0 20 0 20;\n");
        return search;
    }

    public Label navLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 62.0F));
        label.setTextFill(Color.web("#13598b"));
        return label;
    }

    public void makeClickable(Label label) {
        label.setStyle("-fx-cursor: hand;");
        label.setOnMouseEntered((e) -> {
            label.setScaleX(1.08);
            label.setScaleY(1.08);
            label.setTextFill(Color.web("#083c65"));
        });
        label.setOnMouseExited((e) -> {
            label.setScaleX(1.0F);
            label.setScaleY(1.0F);
            label.setTextFill(Color.web("#13598b"));
        });
    }

    public Pane createSun(double x, double y, double r) {
        Pane sun = new Pane();
        Circle circle = new Circle(x, y, r);
        circle.setFill(new LinearGradient(0.0F, 0.0F, 0.0F, 1.0F, true, CycleMethod.NO_CYCLE, new Stop[]{new Stop(0.0F, Color.web("#ffc928")), new Stop(1.0F, Color.web("#ffae25"))}));
        sun.getChildren().add(circle);

        for (int i = 0; i < 8; ++i) {
            double angle = Math.toRadians(i * 45);
            Line ray = new Line(x + Math.cos(angle) * (r + 22.0F), y + Math.sin(angle) * (r + 22.0F), x + Math.cos(angle) * (r + 58.0F), y + Math.sin(angle) * (r + 58.0F));
            ray.setStroke(Color.web("#ffbd28"));
            ray.setStrokeWidth(18.0F);
            ray.setStrokeLineCap(StrokeLineCap.ROUND);
            sun.getChildren().add(ray);
        }

        return sun;
    }

    public Pane createMoon(double x, double y, double r) {
        Pane moon = new Pane();
        Circle base = new Circle(x, y, r);
        base.setFill(new RadialGradient(0.0F, 0.0F, x - r * 0.35, y - r * 0.35, r * 1.15, false, CycleMethod.NO_CYCLE, new Stop[]{new Stop(0.0F, Color.web("#ffffff")), new Stop(0.55, Color.web("#eef2f5")), new Stop(1.0F, Color.web("#cfd7dd"))}));

        Circle shadow = new Circle(x + r * 0.18, y + r * 0.1, r * 0.92);
        shadow.setFill(Color.rgb(170, 180, 190, 0.16));

        Circle crater1 = new Circle(x - r * 0.34, y + r * 0.24, r * 0.22);
        Circle crater2 = new Circle(x + r * 0.33, y - r * 0.37, r * 0.13);
        Circle crater3 = new Circle(x + r * 0.42, y + r * 0.34, r * 0.15);
        Circle crater4 = new Circle(x - r * 0.05, y - r * 0.42, r * 0.09);

        for (Circle crater : new Circle[]{crater1, crater2, crater3, crater4}) {
            crater.setFill(Color.rgb(135, 145, 150, 0.22));
            crater.setStroke(Color.rgb(255, 255, 255, 0.22));
            crater.setStrokeWidth(3.0F);
        }

        Circle clip = new Circle(x, y, r);
        Group moonGroup = new Group(new Node[]{base, shadow, crater1, crater2, crater3, crater4});
        moonGroup.setClip(clip);
        moon.getChildren().add(moonGroup);
        return moon;
    }

    public Pane createCloud(double x, double y, double scale, Color color) {
        Pane cloud = new Pane();
        cloud.setLayoutX(x);
        cloud.setLayoutY(y);
        cloud.setScaleX(scale);
        cloud.setScaleY(scale);

        Circle c1 = new Circle(140.0F, 150.0F, 110.0F, color);
        Circle c2 = new Circle(300.0F, 95.0F, 165.0F, color);
        Circle c3 = new Circle(500.0F, 185.0F, 145.0F, color);
        Circle c4 = new Circle(40.0F, 210.0F, 100.0F, color);
        Ellipse smoothBase = new Ellipse(315.0F, 250.0F, 330.0F, 110.0F);
        smoothBase.setFill(color);

        cloud.getChildren().addAll(new Node[]{c4, c1, c2, c3, smoothBase});
        return cloud;
    }

    public void pulse(Pane node) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(2.4), node);
        st.setFromX(1.0F);
        st.setFromY(1.0F);
        st.setToX(1.035);
        st.setToY(1.035);
        st.setAutoReverse(true);
        st.setCycleCount(-1);
        st.play();
    }

    public void drift(Pane node, double fromX, double toX) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(5.5F), node);
        tt.setFromX(0.0F);
        tt.setToX(toX - fromX);
        tt.setAutoReverse(true);
        tt.setCycleCount(-1);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    public void bounce(Polygon node) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.4), node);
        tt.setFromY(0.0F);
        tt.setToY(-18.0F);
        tt.setAutoReverse(true);
        tt.setCycleCount(-1);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }
}
