package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Card;
import model.Database;

import java.sql.*;
import java.util.ArrayList;

public class CardController {
    ObjectMapper objectMapper = new ObjectMapper();
    Connection db = null;
    Statement statement;

    public CardController() throws SQLException {
        Connection connection = Database.getH2Connection();
        this.db = connection;
        this.statement = this.db.createStatement();
    }

    //--- POST methods ---/
    public String getAllCardsByAccountId(Integer accId) throws SQLException, JsonProcessingException {
        System.out.println("[+] getAllCardsByAccountId");
        PreparedStatement preparedStatement = db.prepareStatement("SELECT * FROM cards WHERE id = ?");
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


}
