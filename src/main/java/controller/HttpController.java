package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpController implements HttpHandler {

    public void start() throws IOException {
        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8765), 0);
        server.createContext("/", new SomeHandler())
        server.start();
    }
}