import javax.xml.transform.Result;
import java.sql.*;
import java.lang.*;

public class PostgresLocal {

    Connection localConnection;

    public PostgresLocal( String db, String username, String password ) {
        setLocalConnection( db, username, password );
    }

    public void setLocalConnection( String db, String username, String password ) {
        try {
            Class.forName( "org.postgresql.Driver" );
            this.localConnection = DriverManager
                    .getConnection( String.format( "jdbc:postgresql://localhost:5432/%s", db ),
                            username, password );
            System.out.println( String.format( "Connected to %s successfully", db ) );
        } catch ( ClassNotFoundException | SQLException e ) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            localConnection.close();
            System.out.println( "Local connection closed" );
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

    /**public void viewTest() {
        String query = String.format( "SELECT * FROM test" );
        try (Statement stmt = localConnection.createStatement() ) {
            ResultSet rs = stmt.executeQuery( query );
            while( rs.next() ) {
                int id = rs.getInt( "id" );
                String name = rs.getString( "name" );
                System.out.println( id + ", " + name );
            }
        }
        catch ( SQLException e ) {
            e.printStackTrace();
        }
    }
     */
}
