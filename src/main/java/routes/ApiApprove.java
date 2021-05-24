package routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import controller.OperatorController;
import controller.TransactionController;
import model.DTO.OperationDTO;
import model.Transaction;
import utilits.HttpHelper;
import utilits.Properties;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiApprove {
    private ObjectMapper objectMapper = new ObjectMapper();
    private final int operatorId = 5;

    public void process(HttpServer server){
        server.createContext("/api/operator", (exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
            String requestUrl = exchange.getRequestURI().toString();
            System.out.println("[!] URL:"+requestUrl);
            if(exchange.getRequestMethod().equals("GET")) return;
            //[:POST] [/api/operator/approve/card]. Activate Card
            if(requestUrl.matches("/api/operator/approve/card$")){
                try {
                    activateCard(exchange);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpHelper.sendHttpResponse(exchange, "message","card activated",200);
            }
            //[:POST] [/api/operator/approve/transactions/]. Get transactions of Account
            if(requestUrl.matches("/api/operator/approve/transaction$")){
                try {
                    approveTransaction(exchange);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpHelper.sendHttpResponse(exchange, "message","transaction approved",200);
            }
            exchange.close();
        }));
    }

    //[:POST] [/api/transactions]. Add new transaction to DB
    private void activateCard(HttpExchange exchange) throws Exception {
        OperationDTO cardDTO= objectMapper.readValue(exchange.getRequestBody(), OperationDTO.class);
        OperatorController operator = new OperatorController(operatorId);
        operator.activateCard(cardDTO);
        System.out.println("[+] Card activated ["+cardDTO.getEntityNumber()+"]");
    }

    //[:POST] [/api/transactions/(\d+)]. Get transaction info by transactionId
    private void approveTransaction(HttpExchange exchange) throws Exception {
        OperationDTO transDTO = objectMapper.readValue(exchange.getRequestBody(), OperationDTO.class);
        OperatorController operator = new OperatorController(operatorId);
        operator.approveTransaction(transDTO);
        System.out.println("[+] Transaction with id = ["+transDTO.getEntityNumber()+"] activated");
    }
}
