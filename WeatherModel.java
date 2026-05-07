import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherModel {
    private final String apiKey = "39c4692421824b45885131102262304";

    public WeatherData fetchWeatherData(String city) throws Exception {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String urlString = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + encodedCity + "&days=2&aqi=no&alerts=no";

        URL url = new URL(urlString);
        Scanner urlScanner = new Scanner(url.openStream());
        StringBuilder jsonData = new StringBuilder();

        while (urlScanner.hasNext()) {
            jsonData.append(urlScanner.nextLine());
        }

        urlScanner.close();

        JSONObject json = new JSONObject(jsonData.toString());
        JSONObject location = json.getJSONObject("location");
        JSONObject current = json.getJSONObject("current");

        WeatherData data = new WeatherData();
        data.locationName = location.getString("name") + ", " + location.getString("region");
        data.currentTemp = current.getDouble("temp_f");
        data.conditionText = current.getJSONObject("condition").getString("text");
        data.lastUpdated = current.getString("last_updated");
        data.isDay = current.getInt("is_day") == 1;

        JSONArray todayHours = json.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONArray("hour");

        data.hourLabels = new String[5];
        data.hourTemps = new double[5];

        int currentHour = Integer.parseInt(data.lastUpdated.substring(11, 13));
        data.minChartTemp = 999.0F;
        data.maxChartTemp = -999.0F;

        for (int i = 0; i < 5; ++i) {
            int hourIndex = Math.min(currentHour + i, 23);
            JSONObject hour = todayHours.getJSONObject(hourIndex);
            String time = hour.getString("time").substring(11);
            double temp = hour.getDouble("temp_f");

            data.hourLabels[i] = time;
            data.hourTemps[i] = temp;
            data.minChartTemp = Math.min(data.minChartTemp, temp);
            data.maxChartTemp = Math.max(data.maxChartTemp, temp);
        }

        if (currentHour > 0) {
            JSONObject previousHour = todayHours.getJSONObject(currentHour - 1);
            data.tempChange = data.currentTemp - previousHour.getDouble("temp_f");
        } else {
            data.tempChange = 0.0F;
        }

        return data;
    }

    public String getWeatherType(String condition) {
        condition = condition.toLowerCase();

        if (!condition.contains("rain") && !condition.contains("drizzle") && !condition.contains("shower") && !condition.contains("thunder")) {
            return !condition.contains("snow") && !condition.contains("sleet") && !condition.contains("ice") && !condition.contains("blizzard") ? "sunny" : "snow";
        } else {
            return "rain";
        }
    }
}
