import java.util.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WeatherGetter {
    private static String city;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String API_KEY = "228c9d836a8cef9782bc4c74e469a680";
    private static final HttpClient client = HttpClient.newHttpClient();

    public WeatherGetter( String city ) {
        WeatherGetter.city = city;
    }

    public Map<String, Object> getWeather() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri( URI.create(
                        String.format( "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=imperial",
                                      city, API_KEY ) )
                    )
                .build();
        try {
            HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );
            Map<String, Object> jsonMap = gson.fromJson( response.body(), HashMap.class );
            return jsonMap;
        }
        catch ( IOException | InterruptedException e ) {
            Error.fatal( "IOException | InterruptedException: HttpResponse" );
        }
        return null;
    }

}
