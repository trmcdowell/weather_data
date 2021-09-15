import javax.xml.transform.Result;
import java.sql.*;
import java.lang.*;

/**
 * Connect to a local postgres database and execute SQL statements
 * @author Thomas McDowell
 * @version 09/15/2021
 */

public class PostgresLocal {

    private static Connection localConnection;

    public PostgresLocal(String db, String username, String password ) { setLocalConnection( db, username, password ); }

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

    public void disconnect() {
        try {
            localConnection.close();
            System.out.println( "Local database connection closed" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public void statement( String sql ) {
        try {
            Statement stmt = localConnection.createStatement();
            stmt.executeUpdate( sql );
            stmt.close();
            System.out.println( "Statement executed" );
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

}
