package routes;

import controller.HttpController;
import model.Database;
import java.sql.Connection;
import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws Exception {
        //Connection to DB
        Connection connection = null;
        try {
            connection = Database.getH2Connection();
            Database.greet(connection);
        } catch (SQLException ex) {
            System.out.println("Database connection failure: " + ex.getMessage());
        }

        //Run Server
        HttpController server = new HttpController();
        server.start();

    }//end main
}
