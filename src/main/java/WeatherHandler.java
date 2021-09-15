import java.util.*;

/**
 * Main class of weather_trends, uses an interactive prompt to collect weather data and view
 * that data over time.
 * @author Thomas McDowell
 * @version 09/15/2021
 */

public class WeatherHandler {

    static WeatherGetter weatherGetter;
    static PostgresLocal weatherDB = new PostgresLocal("weather", "postgres", "postgres" );
    static Scanner scanner = new Scanner( System.in );

    private static void collectData() { System.out.println("foo"); }
    private static void viewTrend() {
        System.out.println("bar");
    }

    public static void main( String[] args ) {

        System.out.println( "Welcome to weather trends!" );
        System.out.println( "Enter a city to collect data from: " );
        String city = scanner.nextLine().toLowerCase();
        weatherGetter = new WeatherGetter( city );

        while ( true ) {

            System.out.println("Enter a command: ");
            String command = scanner.next();

            switch ( command ) {
                case "collect": collectData();
                                break;
                case "trend": viewTrend();
                                break;
                case "exit": System.exit( 0 );
                                break;
                default: Error.warn( "Error: invalid command" );
            }
        }

       /**
        // get weather as HashMap (using map interface)
        weatherGetter = new WeatherGetter( "duluth" );
        Map<String, Object> jsonMap = weatherGetter.getWeather();
        System.out.println( jsonMap.toString() );

        // getting values out of LinkedTreeMap
        Map mainMap = (Map) jsonMap.get( "main" );
        System.out.println( mainMap.get( "temp" ) );

        // connect to local weather db
        PostgresLocal weatherDB = new PostgresLocal("weather", "postgres", "postgres" );
        weatherDB.disconnect();
        */
    }
}
