# whats-going-on-outside
Powered by <a href="https://www.weatherapi.com/" title="Weather API">WeatherAPI.com</a>

Welcome to What's Going On Outside!

This is a Java-based application that allows you to search a city, any city, and recieve a response about the weather in that particular location. What's Going On Outside? uses WeatherAPI in order to recieve accurate data on weather in multiple different countries, cities, and areas.

We used JavaFX for the application's design and usage, to display graphs, current weather, and predicted levels in the future.

Model: Pairs with the API in order to handle data requests. When a user searches for a particular city or area, the API will ping back with a rough estimation of where the user is intending to view. Our model handles that request and allows it to be used.

View: The way the data is displayed; including graphs, visuals, and a representation of the data. The application should be visually responsive to weather changes, allowing for logos and patterns to be changed on the screen when changes are occuring.

Controller: The search function: takes a string from a user, uses that to make a request to WeatherAPI, then sends the data back to the user to be displayed in the View channel.

Observer: Changes to the application happen when Weather/Time looks different. If the location you're viewing is during night, the weather app changes, to create a darker look for the display. If the weather is particularly rainy or snowy, the website will respond to the weather change.

Strategy: Swaps algorithms and display patterns depending on the weather.

Limitations: WeatherAPI limits you to a certain amount of requests and data based on your plan. Data will only display a 5 day period, and potentially the weather. Data is extremely minimalist due to this limitation.

AI was used to create the GUI interface based on human-created mockup images of the application. All other work was created by humans.
