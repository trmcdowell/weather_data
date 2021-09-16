import java.util.*;

/**
 * Main class of weather_trends, uses an interactive prompt to collect weather data from a city and stores it in
 * a local weather database. Also pulls data from the database and creates plots of weather data over time.
 * @author Thomas McDowell
 * @version 09/15/2021
 */

public class WeatherHandler {

    // members
    static WeatherGetter weatherGetter;
    static PostgresLocal weatherDB = new PostgresLocal("weather", "postgres", "postgres" );
    static Scanner scanner = new Scanner( System.in );
    // SQL template for weather database tables
    private static final String weather_table_template = "id serial primary key unique not null, " +
                                                         "date_time timestamptz not null default current_timestamp, " +
                                                         "temp numeric(5,2) not null, " +
                                                         "humidity numeric(5,2) not null, " +
                                                         "pressure numeric(9,2) not null, " +
                                                         "wind_speed numeric(5,2) not null, " +
                                                         "visibility numeric(7,2) not null, " +
                                                         "description varchar not null";

    // methods
    private static void selectCity() {
        System.out.println( "Enter a city to collect data from: " );
        String city = scanner.nextLine().toLowerCase();
        weatherGetter = new WeatherGetter( city );
        weatherDB.statement(
                String.format( "CREATE TABLE IF NOT EXISTS %s (" + weather_table_template + ')', city )
                           );
    }

    private static void pushWeather() {
        // get weather as HashMap (using map interface)
        Map<String, Object> jsonMap = weatherGetter.getWeather();
        //System.out.println( jsonMap.toString() );
        Map mainMap = (Map) jsonMap.get( "main" );
        Map windMap = (Map) jsonMap.get( "wind" );
        Map descMap = (Map) ( (ArrayList) jsonMap.get( "weather" ) ).get(0);
        weatherDB.statement(
                String.format( "INSERT INTO %s (temp, humidity, pressure, wind_speed, visibility, description) " +
                               "values " +
                               '(' +
                               mainMap.get( "temp" ).toString() + ',' +
                               mainMap.get( "humidity" ).toString() + ',' +
                               mainMap.get( "pressure" ).toString() + ',' +
                               windMap.get( "speed" ).toString() + ',' +
                               jsonMap.get( "visibility" ).toString() + ',' +
                                '\'' + descMap.get( "description" ).toString() + '\'' +
                               ')', weatherGetter.getCity()
                             )
                           );
    }

    private static void collectData() {
        System.out.println( "Beginning data collection..." );
        pushWeather();
    }

    private static void viewTrend() { System.out.println( "bar" ); }
    private static void help() { System.out.println( "help" ); }

    public static void main( String[] args ) {

        System.out.println( "Welcome to weather trends!" );
        selectCity();
        while ( true ) {
            System.out.println("Enter a command: ");
            String command = scanner.next().toLowerCase();

            switch ( command ) {
                case "collect" -> collectData();
                case "trend"   -> viewTrend();
                case "help"    -> help();
                case "exit"    -> System.exit( 0 );
                default        -> Error.warn( "Error: invalid command" );
            }
        }
    }

}
