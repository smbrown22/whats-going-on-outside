import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

// Test case for verifying the WeatherApp class
public class WeatherAppTest {
    
    // Stash the real keyboard and screen
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    
    // Create a fake screen to catch your print statements
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void testValidCityWeather() {
        // Simulates typing Raleigh and hitting Enter
        System.setIn(new ByteArrayInputStream("Raleigh\n".getBytes()));
        
        // Runs your exact code
        WeatherApp.main(new String[]{});
        
        // Checks if your code successfully printed the weather data
        String output = outContent.toString();
        assertTrue(output.contains("Weather Data:"));
    }

    @Test
    public void testInvalidCityHandling() {
        // Simulates typing a bad city and hitting Enter
        System.setIn(new ByteArrayInputStream("FakeCity123\n".getBytes()));
        
        // Runs code from WeatherApp
        WeatherApp.main(new String[]{});
        
        // Checks if your catch block successfully printed the error message
        String output = outContent.toString();
        assertTrue(output.contains("Error: Could not retrieve data"));
    }
}