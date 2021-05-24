package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Account;
import model.Card;
import model.Database;
import model.User;
import org.h2.jdbc.JdbcSQLException;
import utilits.SqlHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UserController {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Connection db = null;
    private Statement statement;

    private static String SQL_SELECT_USER_INFO    = "SELECT * FROM users WHERE id = ?";
    private static String SQL_INSERT_NEW_USER     = "INSERT INTO users(login, password, full_name, phone, `role`) VALUES (?, ?, ?, ?, ?)";
    private static String SQL_CHECK_USER_EXIST_BY_PHONE = "SELECT * FROM USERS WHERE phone = ?";
    private static String SQL_CHECK_USER_EXIST_BY_ID = "SELECT * FROM USERS WHERE id = ?";

    public UserController() throws SQLException {
        Connection connection = Database.getH2Connection();
        this.db = connection;
        this.statement = this.db.createStatement();
    }

    //check user exist by Phone
    public boolean checkUserExistByPhone(String phone) throws SQLException {
        PreparedStatement preparedStatement = db.prepareStatement(SQL_CHECK_USER_EXIST_BY_PHONE);
        preparedStatement.setString(1, phone);
        ResultSet resultSet = preparedStatement.executeQuery();
        Integer result = 0;
        while (resultSet.next()) {
            result = resultSet.getInt("cnt");
        }
        return result>0;
    }

    //check user exist by Id
    public boolean checkUserExistById(int userId) throws SQLException {
        PreparedStatement preparedStatement = db.prepareStatement(SQL_CHECK_USER_EXIST_BY_ID);
        preparedStatement.setInt(1, userId);
        ResultSet resultSet = preparedStatement.executeQuery();
        Integer result = 0;
        while (resultSet.next()) {
            result = resultSet.getInt("cnt");
        }
        return result>0;
    }

    //getUserInfo
    public HashMap<String,String> getUserInfo(int userId){
        HashMap<String, String> result = new HashMap<>();
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_SELECT_USER_INFO);
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                result.put("id",            Integer.toString(userId));
                result.put("login",         rs.getString("login"));
                result.put("password",      rs.getString("password"));
                result.put("full_name",     rs.getString("full_name"));
                result.put("phone",         rs.getString("phone"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    //[:POST][/api/users]. Add new user to Database
    public String insertNewUserToDB(User user) throws SQLException {
        //check user.phone exist
        int result = new SqlHelper().countSqlResults("SELECT * FROM users WHERE phone = "+user.getPhone());
        if (result>0) {
            return "User with [phone="+user.getPhone()+"] already exist in Database.";
        }
        //check user.phone exist
        result = new SqlHelper().countSqlResults("SELECT * FROM users WHERE login = "+user.getLogin());
        if (result>0) {
            return "This Login is not available. Please choose another login.";
        }
        //check role is valid
        if( (user.getRole().equals("PERSONAL")) && (user.getRole().equals("CORP")) && (user.getRole().equals("OPERATOR")) ){
            return "Invalid ROLE: "+user.getRole()+". VALID ROLES: PERSONAL, CORP, OPERATOR (IN UPPERCASE)";
        }
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_INSERT_NEW_USER);
            preparedStatement.setString(1,  user.getLogin());
            preparedStatement.setString(2,  user.getPassword());
            preparedStatement.setString(3,  user.getFull_name());
            preparedStatement.setString(4,  user.getPhone());
            preparedStatement.setString(5,  user.getRole() );
            preparedStatement.execute();
        } catch (JdbcSQLException ex) {
            return ex.getMessage();
        }
        return "";
    }


}
