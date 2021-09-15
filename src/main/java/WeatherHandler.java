import java.util.*;

public class WeatherHandler {

    public static void main( String[] args ) {
        // get weather as HashMap (using map interface)
        WeatherGetter weatherGetter = new WeatherGetter( "Duluth" );
        Map<String, Object> jsonMap = weatherGetter.getWeather();
        System.out.println( jsonMap.toString() );

        // getting values out of LinkedTreeMap
        Map mainMap = (Map) jsonMap.get( "main" );
        System.out.println( mainMap.get( "temp" ) );

        // connect to local weather db
        PostgresLocal weatherDB = new PostgresLocal( "weather", "postgres", "postgres" );
        weatherDB.disconnect();
    }
}
