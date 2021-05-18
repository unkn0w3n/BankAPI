package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;


public class HttpController {

    public void start() throws Exception {
        int serverPort = 8000;
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create();
        server.bind(new InetSocketAddress(serverPort), 0);

        HttpContext context = server.createContext("/", new HttpController.EchoHandler());
        //context.setAuthenticator(new HttpController.Auth());
        server.setExecutor(null);
        server.start();

        System.out.println("server started on port:"+serverPort);
    }



    static class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            //get REQUEST_METHOD
            String requestMethod = exchange.getRequestMethod();
            System.out.println("[!] Request method: "+requestMethod);

            //get URL_STRING
            String requestUrl = exchange.getRequestURI().toString();
            System.out.println("[!] URL:"+requestUrl);

            //get CONTENT_TYPE
            Headers headers = exchange.getRequestHeaders();
            String contentType = headers.getFirst("Content-type");
            System.out.println("[!] Content-type: "+contentType);

            //Request JSON text
            String requestBody = requestBodyToString(exchange);
            System.out.println("[!] Post body:"+requestBody);

            //Распределить запросы по контроллерам


        }

        //ResponceBodyToString
        public String requestBodyToString(HttpExchange exchange) throws IOException {
            InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            int b;
            StringBuilder buf = new StringBuilder(512);
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
