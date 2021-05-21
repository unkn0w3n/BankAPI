package utilits;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import model.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;

public class SqlHelper {
    ObjectMapper objectMapper = new ObjectMapper();
    Connection db = null;
    Statement statement;

    public SqlHelper() throws SQLException {
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
