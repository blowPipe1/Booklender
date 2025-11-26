package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import models.Book;
import models.Employee;
import models.LibraryData;
import utils.TemplateRenderer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class BookInfoHandler implements HttpHandler {
    private final LibraryData libraryData;
    private final TemplateRenderer renderer;

    public BookInfoHandler(LibraryData libraryData, TemplateRenderer renderer) {
        this.libraryData = libraryData;
        this.renderer = renderer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        String isbn = null;
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("isbn=")) {
                    isbn = param.substring(5);
                    break;
                }
            }
        }

        String finalIsbn = isbn;
        Book book = libraryData.getBooks().stream()
                .filter(b -> b.getIsbn().equals(finalIsbn))
                .findFirst()
                .orElse(null);

        if (book != null) {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("book", book);
            if (book.isIssued() && book.getIssuedToEmployeeId() != null) {
                Employee employee = libraryData.getEmployees().stream()
                        .filter(e -> e.getEmail().equals(book.getIssuedToEmployeeId()))
                        .findFirst()
                        .orElse(null);

                dataModel.put("issuedTo", employee);
            }

            String response = "";
            try {
                response = renderer.render("book-info.ftlh", dataModel);
                sendResponse(exchange, 200, response, "text/html; charset=UTF-8");
            } catch (TemplateException e) {
                sendResponse(exchange, 500, "Template error: " + e.getMessage());
            }
        } else {
            sendResponse(exchange, 404, "Book not found");
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