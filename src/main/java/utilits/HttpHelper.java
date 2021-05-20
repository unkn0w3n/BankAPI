package utilits;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;

public class HttpHelper {
    private static ObjectMapper objectMapper = new ObjectMapper();

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

    //String answer [only]
    public static void sendHttpResponse(HttpExchange exchange, String answer, int HttpRequestCode) throws IOException {
        answer = "{\"message\":\""+answer+"\"}";
        exchange.sendResponseHeaders(HttpRequestCode, answer.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(answer.getBytes());
        output.flush();
        exchange.close();
    }

    //String type, String answer
    public static void sendHttpResponse(HttpExchange exchange, String type, String answer, int HttpRequestCode) throws IOException {
        if(type=="error") HttpRequestCode = 400;
        //if not JSON => convert in JSON
        if(type!="json"){
            answer = "{\""+type+"\":\""+answer+"\"}";
        }
        exchange.sendResponseHeaders(HttpRequestCode, answer.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(answer.getBytes());
        output.flush();
        exchange.close();
    }


    //HashMap<String,String>
    public static void sendHttpResponse(HttpExchange exchange, HashMap<String,String> answer, int HttpRequestCode) throws IOException {
        String data = objectMapper.writeValueAsString(answer);
        exchange.sendResponseHeaders(HttpRequestCode, data.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(data.getBytes());
        output.flush();
        exchange.close();
    }
}
