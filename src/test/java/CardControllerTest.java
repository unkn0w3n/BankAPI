package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import model.Database;
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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CardControllerTest {

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
    void getAllCardsByUserId() throws SQLException, JsonProcessingException {
        int userId = 1;
        CardController cardContr = new CardController();
        String jsonObjString = cardContr.getAllCardsByUserId(userId);
        System.out.println(jsonObjString);
        assertTrue(jsonObjString.indexOf("4377759828000001")!=-1);
    }

    @Test
    void insertNewCardToDB() throws IOException, SQLException {
        URL url = new URL("http://localhost:8000/api/cards");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonRequest = "{\n" +
                "  \"account_id\": 1,\n" +
                "  \"type\": \"VISA\",\n" +
                "  \"title\": \"VISA CLASSIC\",\n" +
                "  \"number\": \"4377759828000011\",\n" +
                "  \"currency\": \"RUB\",\n" +
                "  \"limit\": 100000,\n" +
                "  \"approved\": true,\n" +
                "  \"active\": true\n" +
                "}";

        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();

        Assertions.assertEquals(connection.getResponseCode(), 200);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8")
        );

        int userId = 1;
        CardController cardContr = new CardController();
        String jsonObjString = cardContr.getAllCardsByUserId(userId);
        System.out.println(jsonObjString);
        assertTrue(jsonObjString.indexOf("4377759828000011")!=-1);
    }

    @Test
    void getCardBalance() throws SQLException, JsonProcessingException {
        Map<String, String> accountData = new HashMap<>();
        String jsonObjString = "";
        CardController cardController = new CardController();
        accountData = cardController.getCardBalance("4377759828000003");
        jsonObjString = objectMapper.writeValueAsString(accountData);
        assertTrue(jsonObjString.indexOf("4377759828000003")!=-1);
    }

    @Test
    void addAmountToCardBalance() throws IOException, SQLException {
        //read Balance Before
        Map<String, String> accountData = new HashMap<>();
        String jsonObjString = "";
        CardController cardController = new CardController();
        accountData = cardController.getCardBalance("4377759828000001");
        Double balance_before = Double.parseDouble(accountData.get("balance"));
        Double addSum = 777.17;

        //SendPOST add Data
        URL url = new URL("http://localhost:8000/api/cards/balance");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        String jsonRequest = "{\n" +
                "  \"operation\": \"ADD_AMOUNT_TO_CARD\",\n" +
                "  \"entity\" : \"CARD\",\n" +
                "  \"entity_number\": \"4377759828000001\",\n" +
                "  \"double\": "+addSum +"\n" +
                "}";
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Assertions.assertEquals(connection.getResponseCode(), 200);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8")
        );

        //read After
        cardController = new CardController();
        accountData = cardController.getCardBalance("4377759828000001");
        Double balance_after = Double.parseDouble(accountData.get("balance"));

        assertEquals(balance_after, balance_before+addSum);
    }


    //        URL url = new URL("http://localhost:8000/clients/bills/11111");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//        Assertions.assertEquals(connection.getResponseCode(), 200);
//        BufferedReader in = new BufferedReader(
//                new InputStreamReader(connection.getInputStream(), "utf-8")
//        );
//        AmountResponseDTO responseDTO = objectMapper.readValue(in, AmountResponseDTO.class);
//        in.close();
//        Assertions.assertEquals(responseDTO.getAmount(), BigDecimal.valueOf(50000.05));
}