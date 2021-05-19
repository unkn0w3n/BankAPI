package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Card;
import model.Database;

import java.sql.*;

public class Helper {
    ObjectMapper objectMapper = new ObjectMapper();
    Connection db = null;
    Statement statement;

    Helper() throws SQLException {
        Connection connection = Database.getH2Connection();
        this.db = connection;
        this.statement = this.db.createStatement();
    }

    public int countSqlResults(String sqlQuery)  throws SQLException {
        int count = 0;
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(sqlQuery);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                count++;
            }
            rs.close();
            return count;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return count;
    }
}
