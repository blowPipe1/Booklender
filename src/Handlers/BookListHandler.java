package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import models.LibraryData;
import utils.TemplateRenderer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class BookListHandler implements HttpHandler {
    private final LibraryData libraryData;
    private final TemplateRenderer renderer;

    public BookListHandler(LibraryData libraryData, TemplateRenderer renderer) {
        this.libraryData = libraryData;
        this.renderer = renderer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("appName", "Библиотечная Система");
        dataModel.put("books", libraryData.getBooks());
        String response = "";
        try {
            response = renderer.render("book-list.ftl", dataModel);
            sendResponse(exchange, 200, response, "text/html; charset=UTF-8");
        } catch (TemplateException e) {
            sendResponse(exchange, 500, "Template error: " + e.getMessage());
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        sendResponse(exchange, statusCode, response, "text/plain; charset=UTF-8");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
