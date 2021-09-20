import java.sql.*;
import java.lang.*;

/**
 * Connect to a local postgres database and execute SQL statements
 * @author Thomas McDowell
 * @version 09/15/2021
 */
public class PostgresLocal {

    protected static Connection localConnection;

    /**
     * PostgresLocal constructor, initializes a local database connection
     * @param db - name of local database
     * @param username - local database username
     * @param password - local database password
     */
    public PostgresLocal(String db, String username, String password ) { setLocalConnection( db, username, password ); }

    /**
     * setLocalConnection sets a local database connection
     * @param db - name of local database
     * @param username - local database username
     * @param password - local database password
     */
    public void setLocalConnection( String db, String username, String password ) {
        try {
            Class.forName( "org.postgresql.Driver" );
            localConnection = DriverManager
                    .getConnection( String.format( "jdbc:postgresql://localhost:5432/%s", db ),
                            username, password );
            System.out.printf( "Connected to %s successfully%n", db );
        } catch ( ClassNotFoundException | SQLException e ) {
            e.printStackTrace();
        }
    }

    /**
     * disconnect from current local connection
     */
    public void disconnect() {
        try {
            localConnection.close();
            System.out.println( "Local database connection closed" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    /**
     * statement executes an SQL statement on the local database that PostgresLocal is currently connected to
     * @param sql - an SQL statement
     */
    public void statement( String sql ) {
        try {
            Statement stmt = localConnection.createStatement();
            stmt.executeUpdate( sql );
            stmt.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

}
