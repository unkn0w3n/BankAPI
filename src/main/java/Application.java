import com.sun.net.httpserver.HttpServer;
import model.Database;
import routes.HttpServerRoutes;

import java.sql.Connection;
import java.sql.SQLException;

public class Application {
    public static void main(String[] args) throws Exception {
        //Connection to DB: create Tables and fill with test data.
        Connection connection = null;
        try {
            connection = Database.getH2Connection();
            Database.createDatabase(connection);
        } catch (SQLException ex) {
            System.out.println("Database connection failure: " + ex.getMessage());
        }
        connection.close();

        //Start Server + Routes parser
        HttpServer httpServer = HttpServer.create();
        HttpServerRoutes server = new HttpServerRoutes(httpServer);
        server.start();
    }
}
