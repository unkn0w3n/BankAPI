package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.DTO.OperationDTO;
import model.Database;
import model.Transaction;
import utilits.SqlHelper;

import java.io.IOException;
import java.sql.*;

public class OperatorController {
    ObjectMapper objectMapper = new ObjectMapper();
    Connection db = null;
    Statement statement;
    private int operatorId;
    private static final String SQL_APPROVE_TRANSACTION   = "UPDATE transactions SET status = 'OPERATOR_APPROVED', approved_by_id = ? WHERE id = ?";
    private static final String SQL_ACTIAVTE_CARD         = "UPDATE cards SET approved = TRUE, active = TRUE WHERE number = ?";

    public OperatorController(int operatorId) throws SQLException {
        Connection connection = Database.getH2Connection();
        this.db = connection;
        this.statement = this.db.createStatement();
        this.operatorId = operatorId;
    }

    //Approve transaction By Id and create transfers
    public void approveTransaction(OperationDTO data) throws SQLException, IOException {
        if(!data.getOperation().equals("APPROVE")) {
            throw new SQLException("To activate Transaction, var \"operation\" must be ACTIVATE");
        }
        if(!data.getEntityType().equals("TRANSACTION")) {
            throw new SQLException("DTO type must be TRANSACTION");
        }
        //Get Transaction by ID
        TransactionController transController = new TransactionController();
        String result = transController.getTransactionInfoById(Integer.parseInt(data.getEntityNumber()));
        Transaction transaction = objectMapper.readValue(result, Transaction.class);
        //UPDATE transaction status
        PreparedStatement preparedStatement = this.db.prepareStatement(SQL_APPROVE_TRANSACTION);
        preparedStatement.setInt(1, this.operatorId);
        preparedStatement.setInt(2, Integer.parseInt(data.getEntityNumber()));
        preparedStatement.execute();
        //MOVE MONEY
        transController.moveMoneyFromOneAccountToAnother(preparedStatement, transaction);
    }

    //activate card
    public boolean activateCard(OperationDTO data) throws Exception {
        if(!data.getOperation().equals("ACTIVATE")) {
            throw new SQLException("To activate card, var \"operation\" must be ACTIVATE");
        }
        if(!data.getEntityType().equals("CARD")) {
            throw new SQLException("DTO type must be card");
        }
        int result = new SqlHelper().countSqlResults("SELECT * FROM cards where number = "+data.getEntityNumber());
        if(result<1) {
            throw new SQLException("CARD with NUMBER "+data.getEntityNumber()+"notExists");
        }
        try {
            PreparedStatement preparedStatement = this.db.prepareStatement(SQL_ACTIAVTE_CARD);
            preparedStatement.setString(1,  data.getEntityNumber());
            preparedStatement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return true;
    }

}