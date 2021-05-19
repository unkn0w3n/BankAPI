package routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import controller.CardController;
import model.Card;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiCards {
    ObjectMapper objectMapper = new ObjectMapper();

    ApiCards(){
    }

    public void process(HttpServer server){
        server.createContext("/api/cards", (exchange -> {
            switch (exchange.getRequestMethod()) {
                //GET /api/cards/(\d+)
                //Get cards by UserId
                case "GET":
                    String requestUrl = exchange.getRequestURI().toString();
                    System.out.println("[!] URL:"+requestUrl);
                    if(!requestUrl.matches("/api/cards/\\d+")){
                        HttpServerRoutes.sendHttpResponse(exchange, "UserId not setted in GET",400);
                        break;
                    }
                    //match regex
                    int userId = 0;
                    Matcher m = Pattern.compile("/api/cards/(\\d+)$").matcher(requestUrl);
                    while(m.find()){
                        userId = m.group(1).length()>0 ? Integer.parseInt(m.group(1)) : 0;
                    }
                    String jsonObjString = null;
                    try {
                        CardController cardContr = new CardController();
                        jsonObjString = cardContr.getAllCardsByUserId(userId);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    System.out.println(jsonObjString);
                    HttpServerRoutes.sendHttpResponse(exchange, jsonObjString,200);
                    System.out.println("[+] GET "+requestUrl+". ShowAllCards With Account id");
                    break;

                //POST /api/cards/
                //ADD NEW CARD TO DATABASE
                case "POST":
                    Card card = objectMapper.readValue(exchange.getRequestBody(), Card.class);
                    try {
                        CardController cardContr = new CardController();
                        String result = cardContr.insertNewCardToDB(card);
                        if(result.length()>0){
                            String err = "Card already exist in database";
                            HttpServerRoutes.sendHttpResponse(exchange, err,200);
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    jsonObjString = objectMapper.writeValueAsString(card);
                    HttpServerRoutes.sendHttpResponse(exchange, jsonObjString,200);
                    System.out.println(jsonObjString);
                    System.out.println("[+] POST /api/cards. Added new Card to Database");
                    break;
                default:
                    HttpServerRoutes.sendHttpResponse(exchange, "Разрешены только POST or GET запросы", 405);
            }
            exchange.close();
        }));
    }

}
