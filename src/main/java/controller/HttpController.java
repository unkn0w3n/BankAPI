package controller;
import com.sun.net.httpserver.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import static routes.Routes.distributeToControllers;

public class HttpController {
    //Start Http server on serverPort
    public void start() throws Exception {
        int serverPort = 8000;
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(serverPort), 0);
        HttpContext context = server.createContext("/", new HttpController.EchoHandler());
        //context.setAuthenticator(new HttpController.Auth());
        server.setExecutor(null);
        server.start();
        System.out.println("server started on port:"+serverPort);
    }

    //Send HTTP Answer
    public void sendHttpResponse(HttpExchange exchange, String answer, int HttpRequestCode) throws IOException {
        exchange.sendResponseHeaders(HttpRequestCode, answer.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(answer.getBytes());
        output.flush();
        exchange.close();
    }

    static class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            //get REQUEST_METHOD + validate TypeOfRequest
            String requestMethod = exchange.getRequestMethod();
            System.out.println("[!] Request method: "+requestMethod);
            if(!"GET".equals(requestMethod) && !"POST".equals(requestMethod)){
                String err = "Некорректный тип запроса: разрешен только POST or GET";
                new HttpController().sendHttpResponse(exchange, err, 400);
            }
            //get URL_STRING
            String requestUrl = exchange.getRequestURI().toString();
            System.out.println("[!] URL:"+requestUrl);
            //get CONTENT_TYPE
            Headers headers = exchange.getRequestHeaders();
            String contentType = headers.getFirst("Content-type");
            System.out.println("[!] Content-type: "+contentType);
            //Request JSON text
            String requestBody = requestBodyToString(exchange);
            if(requestBody.length()>0){
                System.out.println("[!] Post body:"+requestBody);
            }
            //Распределить запрос по контроллерам
            try {
                distributeToControllers(exchange, requestMethod, requestUrl, contentType, requestBody);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }



        //requestBodyToString : Перевести тип запроса из боди в стринг
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




    static class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if ("/forbidden".equals(httpExchange.getRequestURI().toString()))
                return new Failure(403);
            else
                return new Success(new HttpPrincipal("c0nst", "realm"));
        }
    }
}
