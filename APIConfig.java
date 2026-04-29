package model;

public class APIConfig {
    // the one and only spot to hold the data
    private static APIConfig instance;
    
    // the secret key and where we are going on the web
    private final String apiKey = "39c4692421824b45885131102262304"; 
    private final String baseUrl = "http://api.weatherapi.com/v1/forecast.json?key=";

    // keep this private so nobody else can make a new one
    private APIConfig() 

    // the way everyone else gets to use this stuff
    public static APIConfig getInstance() {
        if (instance == null) {
            instance = new APIConfig(); // build it if it is not there yet
        }
        return instance;
    }
}