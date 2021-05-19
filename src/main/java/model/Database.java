package model;
import org.h2.tools.RunScript;

import javax.xml.crypto.Data;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;


public class Database {
    private static Connection connection;
    //CREATE DATABASES
    public static void greet(Connection db) throws SQLException {
        try (Statement dataQuery = db.createStatement()) {
            RunScript.execute(connection, new FileReader("src/main/resources/sql_schema_data/sql_schema.sql"));
            RunScript.execute(connection, new FileReader("src/main/resources/sql_schema_data/test_data.sql"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //RETURN CONNECTIONS
    public static Connection getH2Connection() throws SQLException, SQLException {
        if(Database.connection == null){
            //Database.connection = DriverManager.getConnection("jdbc:h2:mem:");
            Database.connection = DriverManager.getConnection("jdbc:h2:file:./../../h2db;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE");
        }
        return Database.connection;
    }
}

