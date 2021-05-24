package model;
import org.h2.tools.RunScript;
import utilits.Properties;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;


public class Database {
    private static Connection connection;

    //Create Databases + insert Test Data
    public static void createDatabase(Connection db) throws SQLException {
        try {
            RunScript.execute(connection, new FileReader(Properties.SQL_SCHEMA_FILE_PATH));
            RunScript.execute(connection, new FileReader(Properties.SQL_TEST_DATA_FILE_PATH));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Return or create connection
    public static Connection getH2Connection() throws SQLException, SQLException {
        if(Database.connection == null || Database.connection.isClosed()){
            Database.connection = DriverManager.getConnection(Properties.DB_CONNECT_PATH);
        }
        return Database.connection;
    }
}

