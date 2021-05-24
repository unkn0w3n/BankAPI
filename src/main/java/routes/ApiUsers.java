package routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import controller.AccountController;
import controller.UserController;
import model.Account;
import model.User;
import utilits.HttpHelper;
import utilits.Properties;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiUsers {
    private ObjectMapper objectMapper = new ObjectMapper();

    public void process(HttpServer server){
        server.createContext("/api/users", (exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
            String requestUrl = exchange.getRequestURI().toString();
            System.out.println("[!] URL:"+requestUrl);
            switch (exchange.getRequestMethod()) {
                //[:GET] [/api/users/(\d+)]. Get user info.
                case "GET":
                    System.out.println("HEY I AM IN GET!");
                    String userId = "";
                    Matcher m = Pattern.compile("/api/users/(\\d+)$").matcher(requestUrl);
                    while(m.find()){
                        userId = m.group(1).length()>0 ? m.group(1) : "";
                    }
                    if(userId.length() == 0){
                        HttpHelper.sendHttpResponse(exchange, "error","User number not set correctly.",400);
                        break;
                    }
                    //out data
                    String jsonObjString = null;
                    HashMap<String,String> info = new HashMap<>();
                    try {
                        UserController userController = new UserController();
                        info = userController.getUserInfo(Integer.parseInt(userId));
                        if(info.containsKey("error")){
                            HttpHelper.sendHttpResponse(exchange, info,400);
                            break;
                        }
                        jsonObjString = objectMapper.writeValueAsString(info);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    HttpHelper.sendHttpResponse(exchange, "json", jsonObjString,200);
                    System.out.println("[+] GET "+requestUrl+". User info received. id = ["+userId+"]");
                    break;

                //[:POST] [/api/users]. Add new card to database.
                case "POST":
                    System.out.println("HEY I AM IN POST!");
                    User user = objectMapper.readValue(exchange.getRequestBody(), User.class);
                    try {
                        UserController userController = new UserController();
                        String result = userController.insertNewUserToDB(user);
                        if(result.length()>0){
                            HttpHelper.sendHttpResponse(exchange, "error", result,200);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    if(!Properties.debug){
                        jsonObjString = objectMapper.writeValueAsString(user);
                        HttpHelper.sendHttpResponse(exchange, "json", jsonObjString,200);
                        System.out.println(jsonObjString);
                    } else {
                        HttpHelper.sendHttpResponse(exchange, "status","success",200);
                    }
                    System.out.println("[+] POST /api/users. Added new User to Database");
                    break;
                default:
                    HttpHelper.sendHttpResponse(exchange, "error","Only GET and POST Requests are allowed", 405);
            }
            exchange.close();
        }));
    }
}
