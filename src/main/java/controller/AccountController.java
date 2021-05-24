package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Account;
import model.Card;
import model.Database;
import org.h2.jdbc.JdbcSQLException;
import utilits.HttpHelper;
import utilits.SqlHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AccountController {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Connection db = null;
    private Statement statement;

    private static String SQL_SELECT_ACCOUNT_INFO = "SELECT accounts.id, accounts.number, accounts.balance, accounts.user_id, users.full_name FROM accounts, users WHERE accounts.user_id = users.id AND accounts.number = ?";
    private static String SQL_CHECK_ACCOUNT_EXIST = "SELECT COUNT(1) AS CNT FROM accounts WHERE number = ?";
    private static String SQL_INSERT_NEW_ACCOUNT  = "INSERT INTO accounts(title, `number`, currency, user_id, balance) VALUES (?, ?, ?, ?, ?)";

    public AccountController() throws SQLException {
        Connection connection = Database.getH2Connection();
        this.db = connection;
        this.statement = this.db.createStatement();
    }

    //check account exist in database
    public boolean checkAccountExistById(int accId) throws SQLException {
        PreparedStatement preparedStatement = db.prepareStatement(SQL_CHECK_ACCOUNT_EXIST);
        preparedStatement.setInt(1, accId);
        ResultSet resultSet = preparedStatement.executeQuery();
        Integer result = 0;
        while (resultSet.next()) {
            result = resultSet.getInt("cnt");
        }
        return result>0;
    }

    //getAccountInfo with UserData
    public HashMap<String,String> getAccountInfo(String accNumber){
        HashMap<String, String> result = new HashMap<>();
        //validate
        if(accNumber.length()<20) {
            result.put("error","Account number must be 20 digits length");
            return result;
        }
        Double balance = 0.0;
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_SELECT_ACCOUNT_INFO);
            preparedStatement.setString(1, accNumber);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                balance = rs.getDouble("balance");
                result.put("balance",balance.toString());
                result.put("account_number", rs.getString("number"));
                int user_id = rs.getInt("user_id");
                result.put("user_id", Integer.toString(user_id));
                result.put("full_name", rs.getString ("full_name"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    //[:POST][/api/accounts]. Add new Account to Database
    public String insertNewAccountToDB(Account account) throws SQLException {
        //check account exist
        int result = new SqlHelper().countSqlResults("SELECT * FROM accounts where number = "+account.getNumber());
        if (result>0) {
            return "Account with [id="+account.getNumber()+"] already exist in Database.";
        }
        //check linked user exist
        result = new SqlHelper().countSqlResults("SELECT * FROM users where id = "+account.getUser_id());
        System.out.println("USER_RESULT:"+result);
        if (result<1) {
            return "User with [id="+account.getUser_id()+"] not exist. Cant create account linked to non-existence user.";
        }
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_INSERT_NEW_ACCOUNT);
            preparedStatement.setString(1,  account.getTitle());
            preparedStatement.setString(2,  account.getNumber());
            preparedStatement.setString(3,  account.getCurrency());
            preparedStatement.setInt(4,     account.getUser_id());
            preparedStatement.setDouble(5,  account.getBalance());
            preparedStatement.execute();
        } catch (JdbcSQLException ex) {
            return ex.getMessage();
        }
        return "";
    }
}
