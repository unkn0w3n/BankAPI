package routes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import controller.CardController;
import model.Card;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import static routes.Routes.distributeToControllers;

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
            switch (exchange.getRequestMethod()) {
                case "GET":
                    String jsonObjString = null;
                    try {
                        CardController cardContr = new CardController();
                        jsonObjString = cardContr.getAllCardsByAccountId(9);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    System.out.println(jsonObjString);
                    sendHttpResponse(exchange, jsonObjString,200);
                    System.out.println("[+] GET /api/cards. ShowAllCards With Account id");
                    break;


                case "POST":
                    Card card = objectMapper.readValue(exchange.getRequestBody(), Card.class);
                    //System.out.println(card);
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


        //context.setAuthenticator(new HttpController.Auth());
        server.setExecutor(null);
        server.start();
        System.out.println("server started on port:"+serverPort);
    }

    //Send HTTP Answer
    public static void sendHttpResponse(HttpExchange exchange, String answer, int HttpRequestCode) throws IOException {
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
