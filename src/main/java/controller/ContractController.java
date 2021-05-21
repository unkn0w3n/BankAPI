package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Contract;

import java.sql.Connection;
import java.sql.Statement;

public class ContractController {
    private Contract contract;

    public ContractController(Contract contract){
        this.contract = contract;
    }

    ObjectMapper objectMapper = new ObjectMapper();
    Connection db = null;
    Statement statement;

}
