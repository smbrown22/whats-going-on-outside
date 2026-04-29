package model;

public interface ParseStrategy {
    // this just tells the app we need a way to pull data out of the text
    String extractData(String jsonResponse) throws Exception;
}