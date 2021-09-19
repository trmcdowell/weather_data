import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main class of weather_trends, uses an interactive prompt to collect weather data from a city and stores it in
 * a local weather database. Also pulls data from the database and creates plots of weather data over time.
 * @author Thomas McDowell
 * @version 09/15/2021
 */

public class WeatherHandler {

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

    /**
     * selectCity chooses a city to collect data or plot trends from
     */
    private static void selectCity() {
        System.out.println( "Enter city of interest: " );
        String city = scanner.nextLine().toLowerCase();
        Pattern cityPat = Pattern.compile("[a-z]+([\\s]*[a-z]*)*"); // BUG: only matches strings with only alphabetic characters
        Matcher cityMatch = cityPat.matcher( city );
        if ( cityMatch.matches() ) {
            weatherGetter = new WeatherGetter(city);
            weatherDB.statement(
                    String.format("CREATE TABLE IF NOT EXISTS %s (" + weather_table_template + ')',
                                  city.replace(' ', '_')
                                 )
            );
        } else {
            Error.warn("Error: invalid city string");
            selectCity();
        }
    }

    /**
     * pushWeather pushes selected city's weather info to the local database
     */
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
                               ')', weatherGetter.getCity().replace(' ', '_')
                             )
                           );
    }

    /**
     * collectData collects weather data over a user selected time frame
     * @param periodMinutes - number of minutes between data pulls
     * @param collectionTime - total time to collect data in minutes
     */
    private static void collectData( float periodTime, float collectionTime ) {
        Timer timer = new Timer();
        float minute = 60000; // 1 minute in ms
        float period = periodTime * minute;
        timer.schedule(new TimerTask() {
                           long startTime = System.currentTimeMillis();

                           @Override
                           public void run() {
                               if ((System.currentTimeMillis() - startTime) > (collectionTime * minute)) {
                                   System.out.println("Data collection completed");
                                   cancel();
                               } else {
                                   System.out.println("Collecting data...");
                                   pushWeather();
                               }
                           }
                       }, 0, (long) period
        );
        // data collection complete, ready for new command
        System.out.println("Enter a command: ");
    }

    /**
     * viewTend plots weather trends
     */
    private static void viewTrend() {
        System.out.println( "foo" );
        // Plotting complete, ready for new command
        System.out.println("Enter a command: ");
    }

    /**
     * help prints a list of application commands
     */
    private static void help() {
        System.out.println( "Commands:" );
        System.out.println( "collect [PERIOD TIME] [COLLECTION TIME]: collect weather data from selected city every " +
                            "PERIOD TIME minutes for COLLECTION TIME minutes" );
        System.out.println( "trend [TREND]: plot collected TREND data over time" );
        System.out.println( "      Available trends: temperature, humidity, pressure, wind speed, visibility" );
        //System.out.println( "change_city: Change selected city" );
        System.out.println( "help: print available commands" );
        System.out.println( "exit: exit weather trends" );
        // help complete, ready for new command
        System.out.println("Enter a command: ");
    }

    /**
     * main reads in and executes application commands
     */
    public static void main( String[] args ) {
        System.out.println( "Welcome to weather trends!" );
        selectCity();
        System.out.println("Enter a command: ");
        while (true) {
            String command = scanner.next().toLowerCase();
            switch (command) { // BUG: currently have to print enter a command at the end of every command function
                case "collect" -> collectData( scanner.nextFloat(), scanner.nextFloat() );
                case "trend" -> viewTrend();
                //case "change_city" -> selectCity();
                case "help" -> help();
                case "exit" -> System.exit(0);
                default -> Error.warn("Error: invalid command.\nType 'help' for list of valid commands.");
            }
        }
    }

}
