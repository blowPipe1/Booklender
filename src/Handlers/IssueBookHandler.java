package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Book;
import models.LibraryData;
import utils.CookieManager;
import utils.DataParser;
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

public class IssueBookHandler implements HttpHandler {
    private final TemplateRenderer renderer;
    private final LibraryData libraryData;
    private final Map<UUID, String> activeSessions;

    public IssueBookHandler(TemplateRenderer renderer, LibraryData libraryData, Map<UUID, String> activeSessions) {
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
        List<Book> availableBooks;
        synchronized (libraryData) {
            availableBooks = libraryData.getBooks().stream()
                    .filter(book -> !book.isIssued())
                    .collect(Collectors.toList());
        }

        dataModel.put("availableBooks", availableBooks);
        dataModel.put("userEmail", userEmail);

        try {
            String responseHTML = renderer.render("issue_book.ftlh", dataModel);
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
        Map<String, String> formData = DataParser.parseFormData(requestBody);
        String isbn = formData.get("isbn");

        boolean success = issueBook(isbn, userEmail);

        if (success) {
            exchange.getResponseHeaders().set("Location", "/books?message=Book issued successfully");
        } else {
            exchange.getResponseHeaders().set("Location", "/issue-book?error=Issue failed");
        }
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
    }

    private boolean issueBook(String isbn, String userEmail) {
        synchronized (libraryData) {
            List<Book> issuedByUser = libraryData.getBooks().stream()
                    .filter(book -> userEmail.equals(book.getIssuedToEmployeeId()))
                    .collect(Collectors.toList());

            if (issuedByUser.size() >= 2) {
                return false;
            }

            for (Book book : libraryData.getBooks()) {
                if (book.getIsbn().equals(isbn) && !book.isIssued()) {
                    book.setIssued(true);
                    book.setIssuedToEmployeeId(userEmail);
                    return true;
                }
            }
        }
        return false;
    }


}







