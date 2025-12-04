package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import models.LibraryData;
import utils.ResponseSender;
import utils.TemplateRenderer;
import java.io.IOException;
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
            ResponseSender.sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("appName", "Библиотечная Система");
        dataModel.put("books", libraryData.getBooks());
        String response = "";
        try {
            response = renderer.render("book-list.ftlh", dataModel);
            ResponseSender.sendResponse(exchange, 200, response, "text/html; charset=UTF-8");
        } catch (TemplateException e) {
            ResponseSender.sendResponse(exchange, 500, "Template error: " + e.getMessage());
        }
    }

}
