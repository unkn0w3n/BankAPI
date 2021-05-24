package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import model.Database;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import routes.HttpServerRoutes;
import utilits.HttpHelper;
import utilits.Properties;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Connection db = null;
    private Statement statement;
    HttpServer httpServer;

    @BeforeEach
    void setUp() throws IOException {
        //Connection to DB: create Tables and fill with test data.
        Properties.DB_CONNECT_PATH = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
        Connection connection = null;
        try {
            connection = Database.getH2Connection();
            this.db = connection;
            Database.createDatabase(connection);
            this.statement = this.db.createStatement();
        } catch (SQLException ex) {
            System.out.println("Database connection failure on SetUp: " + ex.getMessage());
        }

        httpServer = HttpServer.create();
        HttpServerRoutes server = new HttpServerRoutes(httpServer);
        server.start();
    }

    @AfterEach
    void tearDown() {
        //flush Database
        Connection connection = null;
        try {
            connection = Database.getH2Connection();
            Database.createDatabase(connection);
        } catch (SQLException ex) {
            System.out.println("Database connection failure on TearDown: " + ex.getMessage());
        }
        Properties.DB_CONNECT_PATH = "jdbc:h2:file:./src/main/resources/bankapi_database;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE";
        //Stop server
        httpServer.stop(0);
    }


    @Test
    void checkUserExistByPhone() throws SQLException {
        UserController userController = new UserController();
        boolean actual = userController.checkUserExistById(888);
        boolean expected = false;
        assertEquals(actual, expected);
    }

    @Test
    void checkUserExistById() throws IOException, SQLException {
        UserController userController = new UserController();
        boolean actual = userController.checkUserExistByPhone("79811888777");
        boolean expected = true;
        assertEquals(actual, expected);
    }

    @Test
    void getUserInfo() throws IOException {
        int userId = 1;
        URL url = new URL("http://localhost:8000/api/users/"+userId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        //assert 200 OK
        Assertions.assertEquals(connection.getResponseCode(), 200);
        //read answer
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8")
        );
        StringBuilder responseStr = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            responseStr.append(inputLine);
        in.close();
        System.out.println(responseStr);
        Assertions.assertTrue(responseStr.indexOf("phone\":\"79811888777")!=-1);
    }

    @Test
    void insertNewUserToDB() throws IOException, SQLException {
        String phone = "79081005353";

        User user = new User();
        user.setLogin("p_pushkin");
        user.setPassword("123123");
        user.setFull_name("Александр Сергеевич Пушкин");
        user.setPhone(phone);
        user.setRole("PERSONAL");

        UserController userController = new UserController();
        userController.insertNewUserToDB(user);

        //assert
        boolean userFound = userController.checkUserExistByPhone(phone);
        boolean expected = true;
        assertEquals(userFound, expected);
    }
}