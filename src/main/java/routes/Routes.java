package routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import controller.CardController;
import controller.HttpController;
import model.Card;
import model.Database;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

public class Routes {

    public static void distributeToControllers(HttpExchange exchange, String method, String url, String contentType, String body) throws SQLException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        //Connection to DB
        Connection connection = null;
        try {
            connection = Database.getH2Connection();
        } catch (SQLException ex) {
            System.out.println("Database connection failure: " + ex.getMessage());
        }
        //http Controller
        HttpController http = new HttpController();

        //POST requests
        if("POST".equals(method)){
            switch(url){
                case "/api/cards/add":
                    System.out.println("i am here");
                    Card card = objectMapper.readValue(exchange.getRequestBody(), Card.class);
                    System.out.println(card);
                    http.sendHttpResponse(exchange, "DONE",200);
                    break;
            }
        }

        //GET Requests
        if("GET".equals(method)){
            switch(url){
                case "/api/cards":
                    CardController cardContr = new CardController();
                    String jsonCardsStr = cardContr.getAllCardsByAccountId(1);
                    http.sendHttpResponse(exchange, jsonCardsStr,200);
                    break;
            }
        }
    }

    //Send HTTP Answer
    private void sendHttpResponse(HttpExchange exchange, String answer, int HttpRequestCode) throws IOException {
        exchange.sendResponseHeaders(HttpRequestCode, answer.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(answer.getBytes());
        output.flush();
        exchange.close();
    }
}
