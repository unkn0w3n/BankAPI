package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Card;
import model.Database;
import org.h2.jdbc.JdbcSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CardController {
    private Card card;
    ObjectMapper objectMapper = new ObjectMapper();
    Connection db = null;
    Statement statement;

    private static String SQL_INSERT_NEW_CARD           = "INSERT INTO cards(account_id, type, title, number, currency, `limit`, approved, active) VALUES (?,?,?,?,?,?,?,?)";
    private static String SQL_SELECT_CARD_JOIN_ACCOUNTS = "SELECT * FROM cards INNER JOIN accounts WHERE cards.account_id = accounts.id and accounts.id = ?";
    private static String SQL_SELECT_CARD_BALANCE       = "SELECT accounts.balance, accounts.number as account_number, cards.number as card_number FROM cards INNER JOIN accounts WHERE cards.account_id = accounts.id and cards.number = ?";
    private static String SQL_UPDATE_ACCOUNT_BALANCE    = "UPDATE accounts SET balance = balance + ? WHERE number = ?";

    public CardController() throws SQLException {
        Connection connection = Database.getH2Connection();
        this.db = connection;
        this.statement = this.db.createStatement();
    }

    //[:GET][/api/cards/d+]. Return all cards of user
    public String getAllCardsByUserId(Integer accId) throws SQLException, JsonProcessingException {
        PreparedStatement preparedStatement = db.prepareStatement(SQL_SELECT_CARD_JOIN_ACCOUNTS);
        preparedStatement.setInt(1, accId);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Card> cards = new ArrayList<>();
        while (resultSet.next()) {
            Card card = new Card();
            card.setAccount_id(resultSet.getInt("account_id"));
            card.setType(resultSet.getString("type"));
            card.setTitle(resultSet.getString("title"));
            card.setNumber(resultSet.getString("number"));
            card.setCurrency(resultSet.getString("currency"));
            card.setLimit(resultSet.getDouble("limit"));
            card.setApproved(resultSet.getBoolean("approved"));
            card.setActive(resultSet.getBoolean("active"));
            cards.add(card);
        }
        String result = objectMapper.writeValueAsString(cards);
        return result;
    }

    //[:POST][/api/cards]. Add new Card to Database
    public String insertNewCardToDB(Card card) throws SQLException {
        int result = new Helper().countSqlResults("SELECT * FROM accounts where id = "+card.getAccount_id());
        if (result<1) {
            return "Account with [id="+card.getAccount_id()+"] does not exist in database. Impossible to link card to None account.";
        }
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_INSERT_NEW_CARD);
            preparedStatement.setInt(1,     card.getAccount_id());
            preparedStatement.setString(2,  card.getType());
            preparedStatement.setString(3,  card.getTitle());
            preparedStatement.setString(4,  card.getNumber());
            preparedStatement.setString(5,  card.getCurrency());
            preparedStatement.setDouble(6,  card.getLimit());
            preparedStatement.setBoolean(7, card.getApproved());
            preparedStatement.setBoolean(8, card.getActive());
            preparedStatement.execute();
        } catch (JdbcSQLException ex) {
            //System.out.println("    gm:"        +ex.getMessage());
            return "Card with number "+card.getNumber()+" already exist in database";
        }
        return "";
    }

    //[:POST] /api/cards/
    //return: account_number, card_number, balance
    public HashMap<String,String> getCardBalance(String number) throws SQLException {
        HashMap<String, String> result = new HashMap<>();
        //validate
        if(number.length()<16) {
            result.put("error","Card number must be 16 digits length");
            return result;
        }
        Double balance = 0.0;
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_SELECT_CARD_BALANCE);
            preparedStatement.setString(1, number);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                balance = rs.getDouble("balance");
                result.put("balance",balance.toString());
                result.put("account_number", rs.getString("account_number"));
                result.put("card_number", rs.getString("card_number"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    //[:POST] /api/cards/
    //return: account_number, card_number, balance
    public HashMap<String,String> addAmountToCardBalance(Double addNumber, String cardNumber) throws SQLException {
        HashMap<String, String> result = new HashMap<>();
        result = getCardBalance(cardNumber);
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_UPDATE_ACCOUNT_BALANCE);
            preparedStatement.setDouble(1, addNumber);
            preparedStatement.setString(2, result.get("account_number"));
            preparedStatement.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getCardBalance(cardNumber);
    }
}
