package routes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import controller.CardController;
import model.Card;
import model.DTO.DTOOperation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServerRoutes {
    //Start Http server on serverPort
    public void start() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        //server config
        int serverPort = 8000;
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(serverPort), 0);

        //Post && GET: /api/cards
        server.createContext("/api/cards", (exchange -> {
            //exchange.getRequestHeaders().set("Content-type","application/json");
            switch (exchange.getRequestMethod()) {
                //GET /api/cards/(\d+)
                case "GET":
                    String requestUrl = exchange.getRequestURI().toString();
                    System.out.println("[!] URL:"+requestUrl);
                    if(!requestUrl.matches("/api/cards/\\d+")){
                        sendHttpResponse(exchange, "UserId not setted in GET",400);
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
                    sendHttpResponse(exchange, jsonObjString,200);
                    System.out.println("[+] GET "+requestUrl+". ShowAllCards With Account id");
                    break;

                //ADD NEW CARD TO DATABASE
                case "POST":
                    Card card = objectMapper.readValue(exchange.getRequestBody(), Card.class);
                    try {
                        CardController cardContr = new CardController();
                        String result = cardContr.insertNewCardToDB(card);
                        if(result.length()>0){
                            String err = "Карта с таким номером уже есть в базе";
                            sendHttpResponse(exchange, err,200);
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    jsonObjString = objectMapper.writeValueAsString(card);
                    sendHttpResponse(exchange, jsonObjString,200);
                    System.out.println(jsonObjString);
                    System.out.println("[+] POST /api/cards. Added new Card to Database");
                    break;
                default:
                    sendHttpResponse(exchange, "Разрешены только POST or GET запросы", 405);
            }
            exchange.close();
        }));


        //Post && GET: /api/cards/balance
        server.createContext("/api/cards/balance", (exchange -> {
            String requestUrl = exchange.getRequestURI().toString();
            System.out.println("[!] URL:"+requestUrl);
            //exchange.getRequestHeaders().set("Content-type","application/json");
            switch (exchange.getRequestMethod()) {
                case "GET":
                    break;

                //ADD NEW CARD TO DATABASE
                case "POST":
                    CardController cardController = null;
                    try {
                        cardController = new CardController();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    DTOOperation jsonData = objectMapper.readValue(exchange.getRequestBody(), DTOOperation.class);
                    double amount = jsonData.getAmount();
                    String operation = jsonData.getOperation();
                    String cardNumber = jsonData.getEntityNumber();

                    Map<String, String> accountData = new HashMap<>();
                    String jsonObjString = "";
                    if("GET_CARD_BALANCE".equals(operation)){
                        try {
                            accountData = cardController.getCardBalance(cardNumber);
                            jsonObjString = objectMapper.writeValueAsString(accountData);
                            sendHttpResponse(exchange, jsonObjString,200);
                            System.out.println(jsonObjString);
                            System.out.println("[+] Balance of card: " + cardNumber);
                            break;

                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }


                    if("ADD_AMOUNT_TO_CARD".equals(operation)){
                        try {
                            accountData = cardController.addAmountToCardBalance(amount, cardNumber);
                            jsonObjString = objectMapper.writeValueAsString(accountData);
                            sendHttpResponse(exchange, jsonObjString,200);
                            System.out.println(jsonObjString);
                            System.out.println("[+] Amount added to : " + cardNumber);
                            break;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }

                default:
                    sendHttpResponse(exchange, "Разрешены только POST or GET запросы", 405);
            }
            exchange.close();
        }));


        //context.setAuthenticator(new HttpController.Auth());
        server.setExecutor(null);
        server.start();
        System.out.println("server started on port:"+serverPort);
    }

    //Send HTTP Answer
    public static void sendHttpResponse(HttpExchange exchange, String answer, int HttpRequestCode) throws IOException {
        answer = "{\"message\":\""+answer+"\"}";
        exchange.sendResponseHeaders(HttpRequestCode, answer.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(answer.getBytes());
        output.flush();
        exchange.close();
    }

    //requestBodyToString
    public String requestBodyToString(HttpExchange exchange) throws IOException {
        InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
        BufferedReader br = new BufferedReader(isr);
        int b;
        StringBuilder buf = new StringBuilder(1024);
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }
        br.close();
        isr.close();
        return buf.toString();
    }


}
