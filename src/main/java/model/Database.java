package model;
import org.h2.tools.RunScript;
import utilits.Properties;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.*;


public class Database {
    private static Connection connection;

    //Create Databases + insert Test Data
    public static void createDatabase(Connection db) throws SQLException {
        Reader reader = null;
        Reader reader2 = null;
        InputStream in = Database.class.getResourceAsStream("/sql_schema_data/sql_schema.sql");
        reader = new BufferedReader(new InputStreamReader(in, Charset.forName(StandardCharsets.UTF_8.name())));
        InputStream in2 = Database.class.getResourceAsStream("/sql_schema_data/test_data.sql");
        reader2 = new BufferedReader(new InputStreamReader(in2, Charset.forName(StandardCharsets.UTF_8.name())));
        try {
            RunScript.execute(db, reader);
            RunScript.execute(db, reader2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void xxx(){

//        try {
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            try (Connection db = h2Datasource.setH2Connection();
//            ) {
//                RunScript.execute(db, reader);
//            } catch (SQLException | DatabaseException throwables) {
//                throwables.printStackTrace();
//            }
    }


    //Return or create connection
    public static Connection getH2Connection() throws SQLException, SQLException {
        if(Database.connection == null || Database.connection.isClosed()){
            Database.connection = DriverManager.getConnection(Properties.DB_CONNECT_PATH);
        }
        return Database.connection;
    }




}



