import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import controller.CardController;
import controller.TransactionController;
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
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionControllerTest {
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
    void getTransactionInfoById() throws IOException {
        String accNumber = "40817810255863910001";

        URL url = new URL("http://localhost:8000/api/transactions/account/"+accNumber);
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
        //System.out.println(responseStr);
        Assertions.assertTrue(responseStr.indexOf(accNumber)!=-1);
    }

    @Test
    void getAccountTransactionsInfoByAccNum() throws SQLException {
        String accNumber = "40817810255863910001";
        TransactionController transactionController = new TransactionController();
        String transactions = transactionController.getAccountTransactionsInfoByAccNum(accNumber);
        System.out.println(transactions);
        assertTrue(transactions.indexOf("EXTERNAL_P2P")!=-1);
    }

    @Test
    void createNewTransaction() throws IOException, SQLException {
        String accountTo = "40817810255863910004";
        //balance before
        Map<String, String> accountData = new HashMap<>();
        CardController cardController = new CardController();
        accountData = cardController.getCardBalance("4377759828000001");
        Double balance_before = Double.parseDouble(accountData.get("balance"));

        //HTTP ADD Transaction request
        URL url = new URL("http://localhost:8000/api/transactions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonRequest = "{\n" +
                "  \"t_type\": \"EXTERNAL_P2P\",\n" +
                "  \"account_from\": \"40817810255863910001\",\n" +
                "  \"account_to\": \""+accountTo+"\",\n" +
                "  \"amount\": 790,\n" +
                "  \"approved_by_id\":  777,\n" +
                "  \"status\":  \"AUTO_APPROVED\",\n" +
                "  \"created_at\" : \"\",\n" +
                "  \"updated_at\" : \"\"\n" +
                "}";

        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        //200 => OK
        Assertions.assertEquals(connection.getResponseCode(), 200);

        //balance after
        accountData = cardController.getCardBalance("4377759828000001");
        Double balance_after = Double.parseDouble(accountData.get("balance"));
        assertEquals(balance_before, balance_after+790);

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

        //assert
        assertTrue(responseStr.indexOf("success")!=-1);
    }

    @Test
    void moveMoneyFromOneAccountToAnother() throws SQLException {
        String accNumber = "40817810255863910001";
        Transaction transaction = new Transaction();
        transaction.setT_type("EXTERNAL_P2P");
        transaction.setAccount_from(accNumber);
        transaction.setAccount_to("40817810255863910003");
        transaction.setAmount(777.77);
        transaction.setApproved_by_id(777);
        transaction.setStatus("OPERATOR_APPROVED");
        //create Transaction (move from one to another)
        TransactionController transactionController = new TransactionController();
        transactionController.moveMoneyFromOneAccountToAnother(transaction);
        //try 2 found transaction
        String transactions = transactionController.getAccountTransactionsInfoByAccNum(accNumber);
        System.out.println(transactions);
        assertTrue(transactions.indexOf("AUTO_APPROVED")!=-1);
    }
}