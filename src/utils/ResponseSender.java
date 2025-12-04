package utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseSender {
    public static void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        sendResponse(exchange, statusCode, response, "text/plain; charset=UTF-8");
    }
}
