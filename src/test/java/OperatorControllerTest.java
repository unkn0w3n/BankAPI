import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import controller.TransactionController;
import model.Card;
import model.Database;
import model.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import routes.HttpServerRoutes;
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

class OperatorControllerTest {

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
        System.out.println("[+] Server stopped");
    }

    @Test
    void approveTransaction() throws IOException, SQLException {
        //Get Transaction by ID
        int transNumber = 2;
        TransactionController transController = new TransactionController();
        String result = transController.getTransactionInfoById(transNumber);
        Transaction transaction = objectMapper.readValue(result, Transaction.class);
        String status_before = transaction.getStatus();

        URL url = new URL("http://localhost:8000/api/operator/approve/transaction");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonRequest = "{\n" +
                "  \"operation\": \"APPROVE\",\n" +
                "  \"entity\" : \"TRANSACTION\",\n" +
                "  \"entity_number\": \"2\",\n" +
                "  \"double\": 1\n" +
                "}";

        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
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
        //System.out.println(responseStr);

        //CheckStatus after Transaction
        result = transController.getTransactionInfoById(transNumber);
        transaction = objectMapper.readValue(result, Transaction.class);
        String status_after = transaction.getStatus();

        //assert
        assertNotEquals(status_before, status_after);
    }

    @Test
    void activateCard() throws IOException, SQLException {
        URL url = new URL("http://localhost:8000/api/operator/approve/card");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonRequest = "{\n" +
                "  \"operation\": \"ACTIVATE\",\n" +
                "  \"entity\" : \"CARD\",\n" +
                "  \"entity_number\": \"4377759828000001\",\n" +
                "  \"double\": 1\n" +
                "}";

        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
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
        //System.out.println(responseStr);

        //assert
        assertTrue(responseStr.indexOf("{\"message\":\"card activated\"}")!=-1);

    }
}