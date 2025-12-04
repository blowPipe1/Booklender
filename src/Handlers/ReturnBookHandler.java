package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Book;
import models.LibraryData;
import utils.CookieManager;
import utils.ResponseSender;
import utils.TemplateRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReturnBookHandler implements HttpHandler {
    private final TemplateRenderer renderer;
    private final LibraryData libraryData;
    private final Map<UUID, String> activeSessions;

    public ReturnBookHandler(TemplateRenderer renderer, LibraryData libraryData, Map<UUID, String> activeSessions) {
        this.renderer = renderer;
        this.libraryData = libraryData;
        this.activeSessions = activeSessions;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String userEmail = CookieManager.authenticate(exchange, activeSessions);

        if (userEmail == null) {
            exchange.getResponseHeaders().set("Location", "/login");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            handleGet(exchange, userEmail);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            handlePost(exchange, userEmail);
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
        }
    }

    private void handleGet(HttpExchange exchange, String userEmail) throws IOException {
        Map<String, Object> dataModel = new HashMap<>();
        List<Book> userBooks;
        synchronized (libraryData) {
            userBooks = libraryData.getBooks().stream()
                    .filter(book -> userEmail.equals(book.getIssuedToEmployeeId()))
                    .collect(Collectors.toList());
        }

        dataModel.put("userBooks", userBooks);

        try {
            String responseHTML = renderer.render("return_book.ftlh", dataModel);
            ResponseSender.sendResponse(exchange, 200, responseHTML, "text/html; charset=UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
        }

    }

    private void handlePost(HttpExchange exchange, String userEmail) throws IOException {
        String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        Map<String, String> formData = parseFormData(requestBody);
        String isbn = formData.get("isbn");

        boolean success = returnBook(isbn, userEmail);

        if (success) {
            exchange.getResponseHeaders().set("Location", "/books?message=Book_returned_successfully_hope_u_finished_before_deadline_;)");
        } else {
            exchange.getResponseHeaders().set("Location", "/return-book?error=Issue failed");
        }
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
    }

    private boolean returnBook(String isbn, String userEmail) {
        synchronized (libraryData) {
            for (Book book : libraryData.getBooks()) {
                if (book.getIsbn().equals(isbn) && userEmail.equals(book.getIssuedToEmployeeId())) {
                    book.setIssued(false);
                    book.setIssuedToEmployeeId(null);
                    return true;
                }
            }
        }
        return false;
    }

    private Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        for (String pair : formData.split("&")) {
            String[] entry = pair.split("=");
            if (entry.length == 2) { map.put(entry[0], java.net.URLDecoder.decode(entry[1], StandardCharsets.UTF_8)); }
        }
        return map;
    }
}