package routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import controller.TransactionController;
import model.Transaction;
import utilits.HttpHelper;
import utilits.Properties;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiTransactions {
    ObjectMapper objectMapper = new ObjectMapper();

    ApiTransactions(){
    }

    public void process(HttpServer server){
        server.createContext("/api/transactions", (exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
            String requestUrl = exchange.getRequestURI().toString();
            System.out.println("[!] URL:"+requestUrl);
            switch (exchange.getRequestMethod()) {
                case "GET":
                    //[:GET] [/api/transactions/(\d+)]. Get transaction info by transactionId
                    if(requestUrl.matches("/api/transactions/\\d+$")){
                        getTransactionsById(exchange, requestUrl);
                    }
                    //[:GET] [/api/transactions/(\d+)]. Get transactions of Account
                    if(requestUrl.matches("/api/transactions/account/\\d+$")){
                        getTransactionsByAccountNumber(exchange, requestUrl);
                    }
                    break;
                case "POST":
                    //[:POST] [/api/transactions]. Add new transaction to DB.
                    if(requestUrl.matches("/api/transactions$")){
                        createNewTransaction(exchange);
                    }
                    break;

                default:
                    HttpHelper.sendHttpResponse(exchange, "error","Only GET and POST Requests are allowed", 405);
            }
            exchange.close();
        }));
    }

    //[:POST] [/api/transactions]. Add new transaction to DB
    private void createNewTransaction(HttpExchange exchange) throws IOException {
        Transaction transaction = objectMapper.readValue(exchange.getRequestBody(), Transaction.class);
        try {
            TransactionController transactionController = new TransactionController();
            String result = transactionController.addNewTransactionInfo(transaction);
            if(result.length()>0){
                HttpHelper.sendHttpResponse(exchange, "error", result,200);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if(!Properties.debug){
            String jsonObjString = objectMapper.writeValueAsString(transaction);
            HttpHelper.sendHttpResponse(exchange, "json", jsonObjString,200);
            System.out.println(jsonObjString);
        } else {
            HttpHelper.sendHttpResponse(exchange, "status","success",200);
        }
        System.out.println("[+] POST /api/transactions. Added new transaction data.");
    }

    //[:GET] [/api/transactions/(\d+)]. Get transaction info by transactionId
    private void getTransactionsById(HttpExchange exchange, String requestUrl) throws IOException {
        int transactionId = 0;
        Matcher m = Pattern.compile("/api/transactions/(\\d+)$").matcher(requestUrl);
        while(m.find()){
            transactionId = m.group(1).length()>0 ? Integer.parseInt(m.group(1)) : 0;
        }
        if(transactionId == 0){
            HttpHelper.sendHttpResponse(exchange, "transactionId not set in GET",400);
        } else {
            String jsonObjString = null;
            try {
                TransactionController transactionController = new TransactionController();
                jsonObjString = transactionController.getTransactionInfoById(transactionId);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            HttpHelper.sendHttpResponse(exchange, "json", jsonObjString,200);
            System.out.println("[+] GET "+requestUrl+". show transaction Information");
        }
    }

    //[:GET] [/api/transactions/(\d+)]. Get transactions of Account
    private void getTransactionsByAccountNumber(HttpExchange exchange, String requestUrl) throws IOException {
        String accNumber = "";
        Matcher m = Pattern.compile("/api/transactions/account/(\\d{20})$").matcher(requestUrl);
        while(m.find()){
            accNumber = m.group(1).length()>0 ? m.group(1) : "";
        }
        if(accNumber.length() == 0){
            HttpHelper.sendHttpResponse(exchange, "error","Account number not set correctly. Must be 20 digits.",400);
            return;
        } else {
            //out data
            String transactions = "";
            try {
                TransactionController transactionController = new TransactionController();
                transactions = transactionController.getAccountTransactionsInfoByAccNum(accNumber);
                if (transactions.matches("error")) {
                    HttpHelper.sendHttpResponse(exchange, "error", transactions, 400);
                    return;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            HttpHelper.sendHttpResponse(exchange, "json", transactions, 200);
            System.out.println("[+] GET " + requestUrl + ". Transactions info received for AccNumber = [" + accNumber + "]");
        }
    }
}
