package routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpServerRoutes {
    //Start Server and Parse Routes
    public void start() throws IOException {
        //server config
        int serverPort = 8000;
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(serverPort), 0);

        //Post && GET: /api/cards
        ApiCards apiCards = new ApiCards();
        apiCards.process(server);

        //Post && GET: /api/cards/balance
        ApiCardsBalance apiCardsBalance = new ApiCardsBalance();
        apiCardsBalance.process(server);

        //Server start
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

//    //requestBodyToString
//    public String requestBodyToString(HttpExchange exchange) throws IOException {
//        InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
//        BufferedReader br = new BufferedReader(isr);
//        int b;
//        StringBuilder buf = new StringBuilder(1024);
//        while ((b = br.read()) != -1) {
//            buf.append((char) b);
//        }
//        br.close();
//        isr.close();
//        return buf.toString();
//    }


}
