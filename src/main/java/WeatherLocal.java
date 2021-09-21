import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WeatherLocal extends PostgresLocal {
    /**
     * WeatherLocal constructor, initializes a local weather database connection
     * @param db       - name of local database
     * @param username - local database username
     * @param password - local database password
     */

    public WeatherLocal(String db, String username, String password) {
        super(db, username, password);
    }

    public void printTable( String table ) {
        String QUERY = String.format( "SELECT * FROM %s", table.replace(' ', '_') );
        try {
            Statement stmt = localConnection.createStatement();
            ResultSet rs = stmt.executeQuery( QUERY );
            System.out.println( "Date/Time                     | Temperature | Humidity | Pressure | Wind Speed |" +
                                " Visibility | Description" );
            System.out.println( "------------------------------|-------------|----------|----------|------------|" +
                                "------------|----------------------" );
            while( rs.next() ) {
                System.out.println(
                        String.format( "%s | %s       | %s     | %s   | %s        | %s    | %s",
                                rs.getString("date_time"),
                                rs.getFloat("temperature"),
                                rs.getFloat("humidity"),
                                rs.getFloat("pressure"),
                                rs.getFloat("wind_speed"),
                                rs.getFloat("visibility"),
                                rs.getString("description")
                        )
                );
            }
        } catch ( SQLException e ) {
            Error.warn("Error: table does not exist");
        }
    }

    public void printColumn( String label, String table ) {
        String QUERY = String.format( "SELECT date_time, %s FROM %s", label, table );
        try {
            Statement stmt = localConnection.createStatement();
            ResultSet rs = stmt.executeQuery( QUERY );
            if ( label.equals( "date_time" ) ) {
                Error.warn( "Error: date not a selectable column" );
            }
            else if ( label.equals( "description" ) ) {
                System.out.println( "Date/Time                     | Description");
                System.out.println( "------------------------------|-----------------------" );
                while( rs.next() )  System.out.println(
                        String.format( "%s | %s", rs.getString("date_time"),
                                                  rs.getString("description") )
                );
            }
            else {
                System.out.println( String.format( "Date/Time                     | %s", label ) );
                System.out.println( "------------------------------|-----------------------" );
                while( rs.next() ) System.out.println(
                        String.format( "%s | %s", rs.getString("date_time"),
                                                  rs.getFloat(label) )
                );
            }
        } catch ( SQLException e ) {
            Error.warn( "Error: column does not exist" );
        }
    }
}
