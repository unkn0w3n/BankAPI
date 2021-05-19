import model.Database;
import routes.HttpServerRoutes;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Application {
    public static void main(String[] args) throws Exception {
        //Connection to DB. Create and fill tables.
        Connection connection = null;
        try {
            connection = Database.getH2Connection();
            Database.greet(connection);
        } catch (SQLException ex) {
            System.out.println("Database connection failure: " + ex.getMessage());
        }

        //Start Server + Routes parser
        HttpServerRoutes server = new HttpServerRoutes();
        server.start();

        //sleep
        TimeUnit.SECONDS.sleep(2);
    }
}
