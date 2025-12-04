package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import models.Book;
import models.Employee;
import models.LibraryData;
import utils.ResponseSender;
import utils.TemplateRenderer;
import java.io.IOException;
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
            ResponseSender.sendResponse(exchange, 405, "Method Not Allowed");
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
                ResponseSender.sendResponse(exchange, 200, response, "text/html; charset=UTF-8");
            } catch (TemplateException e) {
                ResponseSender.sendResponse(exchange, 500, "Template error: " + e.getMessage());
            }
        } else {
            ResponseSender.sendResponse(exchange, 404, "Book not found");
        }
    }

}