package routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import controller.CardController;
import model.Card;
import utilits.HttpHelper;
import utilits.Properties;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiCards {
    ObjectMapper objectMapper = new ObjectMapper();

    ApiCards(){
    }

    public void process(HttpServer server){
        server.createContext("/api/cards", (exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
            String requestUrl = exchange.getRequestURI().toString();
            System.out.println("[!] URL:"+requestUrl);
            switch (exchange.getRequestMethod()) {
                //[:GET] [/api/cards/(\d+)]. Get cards of UserId
                case "GET":
                    int userId = 0;
                    Matcher m = Pattern.compile("/api/cards/(\\d+)$").matcher(requestUrl);
                    while(m.find()){
                        userId = m.group(1).length()>0 ? Integer.parseInt(m.group(1)) : 0;
                    }
                    if(userId == 0){
                        HttpHelper.sendHttpResponse(exchange, "UserId not set in GET",400);
                        break;
                    }
                    String jsonObjString = null;
                    try {
                        CardController cardContr = new CardController();
                        jsonObjString = cardContr.getAllCardsByUserId(userId);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    HttpHelper.sendHttpResponse(exchange, "json", jsonObjString,200);
                    System.out.println("[+] GET "+requestUrl+". ShowAllCards With Account id");
                    break;

                //[:POST] [/api/cards/]. ADD NEW CARD TO DATABASE
                case "POST":
                    Card card = objectMapper.readValue(exchange.getRequestBody(), Card.class);
                    try {
                        CardController cardContr = new CardController();
                        String result = cardContr.insertNewCardToDB(card);
                        if(result.length()>0){
                            HttpHelper.sendHttpResponse(exchange, "error", result,400);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    if(Properties.debug) {
                        jsonObjString = objectMapper.writeValueAsString(card);
                        HttpHelper.sendHttpResponse(exchange, "json", jsonObjString, 200);
                        System.out.println(jsonObjString);
                    } else{
                        HttpHelper.sendHttpResponse(exchange, "status","success", 200);
                    }
                    System.out.println("[+] POST /api/cards. Added new Card to Database");
                    break;
                default:
                    HttpHelper.sendHttpResponse(exchange, "Разрешены только POST or GET запросы", 405);
            }
            exchange.close();
        }));
    }

}
