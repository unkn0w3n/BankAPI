package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Account;
import model.Card;
import model.Database;
import model.Transaction;
import org.h2.jdbc.JdbcSQLException;
import utilits.HttpHelper;
import utilits.SqlHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TransactionController {
    ObjectMapper objectMapper = new ObjectMapper();
    Connection db = null;
    Statement statement;

    private static String SQL_GET_ACCOUNT_TRANSACTIONS     = "SELECT * FROM transactions WHERE account_from = ?";
    private static String SQL_GET_TRANSACTION_INFO_BY_ID   = "SELECT * FROM transactions WHERE id = ?";
    private static String SQL_INSERT_NEW_TRANSACTION       = "INSERT INTO transactions(t_type, account_from, account_to, amount, approved_by_id, status) VALUES (?, ?, ?, ?, ?, ?)";

    public TransactionController() throws SQLException {
        Connection connection = Database.getH2Connection();
        this.db = connection;
        this.statement = this.db.createStatement();
    }

    public String getTransactionInfoById(int transactionId){
        String result = "";
        Transaction transaction = new Transaction();
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_GET_TRANSACTION_INFO_BY_ID);
            preparedStatement.setInt(1, transactionId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                transaction.setT_type(rs.getString("t_type"));
                transaction.setAccount_from(rs.getString("account_from"));
                transaction.setAccount_to(rs.getString("account_to"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setApproved_by_id(rs.getInt("approved_by_id"));
                transaction.setStatus(rs.getString("status"));

                result = objectMapper.writeValueAsString(transaction);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public String getAccountTransactionsInfoByAccNum(String accNumber){
        String result = "";
        //validate
        if(accNumber.length()<20) {
            result = "error: Account number must be 20 digits length";
            return result;
        }
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_GET_ACCOUNT_TRANSACTIONS);
            preparedStatement.setString(1, accNumber);
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<Transaction> transactions = new ArrayList<>();
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setT_type(rs.getString("t_type"));
                transaction.setAccount_from(rs.getString("account_from"));
                transaction.setAccount_to(rs.getString("account_to"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setApproved_by_id(rs.getInt("approved_by_id"));
                transaction.setStatus(rs.getString("status"));
                transactions.add(transaction);
            }
            result = objectMapper.writeValueAsString(transactions);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    //[:POST][/api/transactions]. Add new TransactionInfo to Database
    public String addNewTransactionInfo(Transaction transaction) throws SQLException {
        //check account_from exist
        int result = new SqlHelper().countSqlResults("SELECT * FROM accounts where number = "+transaction.getAccount_from());
        if (result<1) {
            return "Account with [id="+transaction.getAccount_from()+"] not exists in Database.";
        }
        //check account_to exist
        result = new SqlHelper().countSqlResults("SELECT * FROM accounts where number = "+transaction.getAccount_to());
        if (result<1) {
            return "Account with [id="+transaction.getAccount_to()+"] not exists in Database.";
        }

        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_INSERT_NEW_TRANSACTION);
            preparedStatement.setString(1,  transaction.getT_type());
            preparedStatement.setString(2,  transaction.getAccount_from());
            preparedStatement.setString(3,  transaction.getAccount_to());
            preparedStatement.setDouble(4,  transaction.getAmount());
            preparedStatement.setInt(5,     transaction.getApproved_by_id());
            preparedStatement.setString(6,  transaction.getStatus());
            preparedStatement.execute();
        } catch (JdbcSQLException ex) {
            return ex.getMessage();
        }
        return "";
    }

}
