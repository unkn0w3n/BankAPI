package routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import controller.AccountController;
import controller.CardController;
import model.Account;
import model.Card;
import utilits.HttpHelper;
import utilits.Properties;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiAccounts {
    private ObjectMapper objectMapper = new ObjectMapper();

    public void process(HttpServer server){
        server.createContext("/api/accounts", (exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
            String requestUrl = exchange.getRequestURI().toString();
            System.out.println("[!] URL:"+requestUrl);
            switch (exchange.getRequestMethod()) {
                //[:GET] [/api/accounts/(\d{20})]. Get accounts info.
                case "GET":
                    String accNumber = "";
                    Matcher m = Pattern.compile("/api/accounts/(\\d{20})$").matcher(requestUrl);
                    while(m.find()){
                        accNumber = m.group(1).length()>0 ? m.group(1) : "";
                    }
                    if(accNumber.length() == 0){
                        HttpHelper.sendHttpResponse(exchange, "error","Account number not set correctly. Must be 20 digits.",400);
                        break;
                    }
                    //out data
                    String jsonObjString = null;
                    HashMap<String,String> accounts = new HashMap<>();
                    try {
                        AccountController accountController = new AccountController();
                        accounts = accountController.getAccountInfo(accNumber);
                        if(accounts.containsKey("error")){
                            HttpHelper.sendHttpResponse(exchange, accounts,400);
                            break;
                        }
                        jsonObjString = objectMapper.writeValueAsString(accounts);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    HttpHelper.sendHttpResponse(exchange, "json", jsonObjString,200);
                    System.out.println("[+] GET "+requestUrl+". Account info received. number = ["+accNumber+"]");
                    break;

                //[:POST] [/api/accounts]. Add new card to database.
                case "POST":
                    Account account = objectMapper.readValue(exchange.getRequestBody(), Account.class);
                    try {
                        AccountController accountController = new AccountController();
                        String result = accountController.insertNewAccountToDB(account);
                        if(result.length()>0){
                            HttpHelper.sendHttpResponse(exchange, "error", result,200);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    if(!Properties.debug){
                        jsonObjString = objectMapper.writeValueAsString(account);
                        HttpHelper.sendHttpResponse(exchange, "json", jsonObjString,200);
                        System.out.println(jsonObjString);
                    } else {
                        HttpHelper.sendHttpResponse(exchange, "status","success",200);
                    }
                    System.out.println("[+] POST /api/cards. Added new Card to Database");
                    break;
                default:
                    HttpHelper.sendHttpResponse(exchange, "error","Only GET and POST Requests are allowed", 405);
            }
            exchange.close();
        }));
    }


}
