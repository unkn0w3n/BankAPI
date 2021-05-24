package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import model.Database;
import org.junit.jupiter.api.*;
import routes.HttpServerRoutes;
import utilits.Properties;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AccountControllerTest {
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
    void getAccountInfo() throws SQLException {
        HashMap<String,String> accounts = new HashMap<>();
        AccountController accountController = new AccountController();
        accounts = accountController.getAccountInfo("40817810255863910001");
        String real = accounts.get("account_number");
        String expected = "40817810255863910001";
        assertEquals(expected, real);
    }

    @Test
    void insertNewAccountToDB() throws IOException, SQLException {
        URL url = new URL("http://localhost:8000/api/accounts");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonRequest = "{\n" +
                "  \"title\": \"Personal account\",\n" +
                "  \"number\": \"40817810255863910007\",\n" +
                "  \"currency\": \"RUB\",\n" +
                "  \"user_id\": 2,\n" +
                "  \"balance\": 100.00\n" +
                "}";

        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();

        Assertions.assertEquals(connection.getResponseCode(), 200);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8")
        );

        AccountController accountController = new AccountController();
        HashMap accounts = accountController.getAccountInfo("40817810255863910007");
        String real = (String) accounts.get("account_number");
        String expected = "40817810255863910007";
        assertEquals(expected, real);
    }

    @Test
    void checkAccountExistByIdTest() throws SQLException {
        AccountController accountController = new AccountController();
        boolean actual = accountController.checkAccountExistById(777);
        boolean expected = false;
        assertFalse(actual);
    }
}