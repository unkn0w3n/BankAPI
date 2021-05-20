package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Account;
import model.Card;
import model.Database;
import model.Payment;
import org.h2.jdbc.JdbcSQLException;
import utilits.HttpHelper;
import utilits.SqlHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PaymentController {
    ObjectMapper objectMapper = new ObjectMapper();
    Connection db = null;
    Statement statement;

    private static String SQL_GET_ACCOUNT_PAYMENTS     = "SELECT * FROM payments WHERE account_from = ?";
    private static String SQL_GET_PAYMENT_INFO_BY_ID   = "SELECT * FROM payments WHERE id = ?";
    private static String SQL_INSERT_NEW_PAYMENT       = "INSERT INTO payments(p_type, account_from, account_to, amount, approved_by_id, status) VALUES (?, ?, ?, ?, ?, ?)";

    public PaymentController() throws SQLException {
        Connection connection = Database.getH2Connection();
        this.db = connection;
        this.statement = this.db.createStatement();
    }

    public String getPaymentInfoById(int paymentId){
        String result = "";
        Payment payment = new Payment();
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_GET_PAYMENT_INFO_BY_ID);
            preparedStatement.setInt(1, paymentId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                payment.setP_type(rs.getString("p_type"));
                payment.setAccount_from(rs.getString("account_from"));
                payment.setAccount_to(rs.getString("account_to"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setApproved_by_id(rs.getInt("approved_by_id"));
                payment.setStatus(rs.getString("status"));

                result = objectMapper.writeValueAsString(payment);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public String getAccountPaymentsInfoByAccNum(String accNumber){
        String result = "";
        //validate
        if(accNumber.length()<20) {
            result = "error: Account number must be 20 digits length";
            return result;
        }
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_GET_ACCOUNT_PAYMENTS);
            preparedStatement.setString(1, accNumber);
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<Payment> payments = new ArrayList<>();
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setP_type(rs.getString("p_type"));
                payment.setAccount_from(rs.getString("account_from"));
                payment.setAccount_to(rs.getString("account_to"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setApproved_by_id(rs.getInt("approved_by_id"));
                payment.setStatus(rs.getString("status"));
                payments.add(payment);
                result = objectMapper.writeValueAsString(payments);
                return result;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }


    //[:POST][/api/payments]. Add new PaymentInfo to Database
    public String addNewPaymentInfo(Payment payment) throws SQLException {
        //check account_from exist
        int result = new SqlHelper().countSqlResults("SELECT * FROM accounts where number = "+payment.getAccount_from());
        if (result<1) {
            return "Account with [id="+payment.getAccount_from()+"] not exists in Database.";
        }
        //check account_to exist
        result = new SqlHelper().countSqlResults("SELECT * FROM accounts where number = "+payment.getAccount_to());
        if (result<1) {
            return "Account with [id="+payment.getAccount_to()+"] not exists in Database.";
        }

        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_INSERT_NEW_PAYMENT);
            preparedStatement.setString(1,  payment.getP_type());
            preparedStatement.setString(2,  payment.getAccount_from());
            preparedStatement.setString(3,  payment.getAccount_to());
            preparedStatement.setDouble(4,  payment.getAmount());
            preparedStatement.setInt(5,     payment.getApproved_by_id());
            preparedStatement.setString(6,  payment.getStatus());
            preparedStatement.execute();
        } catch (JdbcSQLException ex) {
            return ex.getMessage();
        }
        return "";
    }

}
