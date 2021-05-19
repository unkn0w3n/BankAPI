package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Account;
import model.Card;
import model.Database;

import java.sql.*;
import java.util.ArrayList;

public class AccountController {
    ObjectMapper objectMapper = new ObjectMapper();
    Connection db = null;
    Statement statement;

    AccountController(Account account) throws SQLException {
        Connection connection = Database.getH2Connection();
        this.db = connection;
        this.statement = this.db.createStatement();
    }

    //check account exist in database
    public boolean checkAccountExistById(int accId) throws SQLException {
        PreparedStatement preparedStatement = db.prepareStatement("SELECT COUNT(1) AS CNT FROM accounts WHERE acc_number = ?");
        preparedStatement.setInt(1, accId);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Card> cards = new ArrayList<>();
        Integer result = 0;
        while (resultSet.next()) {
            result = resultSet.getInt("cnt");
        }
        return result>0;
    }




}
