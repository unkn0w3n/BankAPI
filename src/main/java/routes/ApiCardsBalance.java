package routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import controller.CardController;
import model.DTO.OperationDTO;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ApiCardsBalance {
    ObjectMapper objectMapper = new ObjectMapper();

    ApiCardsBalance(){

    }

    public void process(HttpServer server){
        //Post && GET: /api/cards/balance
        server.createContext("/api/cards/balance", (exchange -> {
            String requestUrl = exchange.getRequestURI().toString();
            System.out.println("[!] URL:"+requestUrl);
            //exchange.getRequestHeaders().set("Content-type","application/json");
            switch (exchange.getRequestMethod()) {
                case "GET":
                    break;

                case "POST":
                    CardController cardController = null;
                    try {
                        cardController = new CardController();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    OperationDTO jsonData = objectMapper.readValue(exchange.getRequestBody(), OperationDTO.class);
                    double amount = jsonData.getAmount();
                    String operation = jsonData.getOperation();
                    String cardNumber = jsonData.getEntityNumber();

                    Map<String, String> accountData = new HashMap<>();
                    String jsonObjString = "";
                    if("GET_CARD_BALANCE".equals(operation)){
                        try {
                            accountData = cardController.getCardBalance(cardNumber);
                            jsonObjString = objectMapper.writeValueAsString(accountData);
                            HttpServerRoutes.sendHttpResponse(exchange, jsonObjString,200);
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
                            HttpServerRoutes.sendHttpResponse(exchange, jsonObjString,200);
                            System.out.println(jsonObjString);
                            System.out.println("[+] Amount added to : " + cardNumber);
                            break;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }

                default:
                    HttpServerRoutes.sendHttpResponse(exchange, "Разрешены только POST or GET запросы", 405);
            }
            exchange.close();
        }));
    }
}
