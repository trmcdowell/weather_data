import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main class of weather_trends, uses an interactive prompt to collect weather data from a city and stores it in
 * a local weather database. Also pulls data from the database and prints collected data for viewing.
 * @author Thomas McDowell
 * @version 09/15/2021
 */

public class WeatherHandler {

    static WeatherGetter weatherGetter = new WeatherGetter(""); // default string before user selects city
    static WeatherLocal weatherDB = new WeatherLocal("weather", "postgres", "postgres" );
    static Scanner scanner = new Scanner( System.in );
    // SQL template for weather database tables
    private static final String weather_table_template = "id serial primary key unique not null, " +
                                                         "date_time timestamptz not null default current_timestamp, " +
                                                         "temperature numeric(5,2) not null, " +
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
            weatherGetter.setCity( city );
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
                String.format( "INSERT INTO %s (temperature, humidity, pressure, wind_speed, visibility, description)" +
                               "values " +
                               '(' +
                               mainMap.get( "temp" ).toString() + ',' +
                               mainMap.get( "humidity" ).toString() + ',' +
                               mainMap.get( "pressure" ).toString() + ',' +
                               windMap.get( "speed" ).toString() + ',' +
                               jsonMap.get( "visibility" ).toString() + ',' +
                                '\'' + descMap.get( "description" ).toString() + '\'' +
                               ')', weatherGetter.getCity().replace( ' ', '_' )
                             )
                           );
    }

    /**
     * collectData collects weather data over a user selected time frame
     * @param periodTime - number of minutes between data pulls
     * @param collectionTime - total time to collect data in minutes
     */
    private static void collectData( float periodTime, float collectionTime ) {
        if ( periodTime >= 1 && collectionTime >= periodTime ) {
            Timer timer = new Timer();
            float minute = 60000; // 1 minute in ms
            float period = periodTime * minute;
            System.out.println("Collecting data...");
            timer.schedule( new TimerTask() {
                               long startTime = System.currentTimeMillis();

                               @Override
                               public void run() {
                                   if ((System.currentTimeMillis() - startTime) > (collectionTime * minute)) {
                                       System.out.println("Data collection completed");
                                       cancel();
                                       // data collection complete, ready for new command
                                       System.out.println("Enter a command: ");
                                   } else {
                                       pushWeather();
                                   }
                               }
                           }, 0, (long) period
            );
        } else {
            Error.warn( "Error: period time must be greater than or equal to 1 and less than collection time" );
            System.out.println("Enter a command: ");
        }
    }

    /**
     * viewData prints a column of previously colleccted weather data with corresponding timestamps, or all weather
     * data with timestamps
     * @param label - column to view, all for whole table
     */
    private static void viewData( String label ) {
        if ( !label.equals("all") ) weatherDB.printColumn( label, weatherGetter.getCity() );
        else weatherDB.printTable( weatherGetter.getCity() );
        // Plotting complete, ready for new command
        System.out.println("Enter a command: ");
    }

    /**
     * help prints a list of application commands
     */
    private static void help() {
        System.out.println( "Commands:" );
        System.out.println( "collect [PERIOD TIME] [COLLECTION TIME]: collect weather data from selected city every " +
                            "[PERIOD TIME] minutes for [COLLECTION TIME] minutes" );
        System.out.println( "view [DATA]: view collected data, all for whole data set" );
        System.out.println( "      Available data: temperature, humidity, pressure, wind speed, visibility" );
        //System.out.println( "city: select a new city" );
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
        System.out.println( "Type 'help' for a list of valid commands" );
        selectCity();
        System.out.println("Enter a command: ");
        while (true) {
            String command = scanner.next().toLowerCase();
            switch (command) { // BUG: currently have to print enter a command at the end of every command function
                case "collect" -> collectData( scanner.nextFloat(), scanner.nextFloat() );
                case "view" -> viewData( scanner.next() );
                case "help" -> help();
                //case "city" -> selectCity();
                case "exit" -> System.exit(0);
                default -> Error.warn("Error: invalid command.\nType 'help' for a list of valid commands.");
            }
        }
    }
}
