import java.util.Scanner;
import java.net.URL;
public class WeatherApp{
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter a city name: ");
        String city = input.nextLine();

        String apiKey = "39c4692421824b45885131102262304";
        
        String urlString = "http://api.weatherapi.com/v1/forecast.json?key=" + apiKey +"&q=" + city + "&days=1&aqi=no&alerts=no";

        try {
            

            URL url = new URL(urlString); 
            Scanner urlScanner = new Scanner(url.openStream());

            StringBuilder jsonData = new StringBuilder();
            
            while (urlScanner.hasNext()) {
                jsonData.append(urlScanner.nextLine());
            }
            
            urlScanner.close();

            System.out.println("Weather Data: ");

            System.out.println(jsonData.toString());

        } catch (Exception ex) {

            System.out.println("Error: Could not retrieve data. " + ex.getMessage());

        }
        
        input.close();
    }
}