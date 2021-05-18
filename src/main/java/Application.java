import controller.HttpController;
import model.Database;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;


public class Application {
    public static void main(String[] args) throws Exception {
        //Connection to DB
        Connection connection = null;
        try {
            connection = Database.getH2Connection();
            Database.greet(connection);
        } catch (SQLException ex) {
            System.out.println("Database connection failure: " + ex.getMessage());
        }
        //Run Server and parse Requests
        HttpController server = new HttpController();
        server.start();

        //sleep
        TimeUnit.SECONDS.sleep(2);
    }
}
