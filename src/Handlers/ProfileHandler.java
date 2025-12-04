package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Book;
import models.Employee;
import models.LibraryData;
import utils.CookieManager;
import utils.ResponseSender;
import utils.TemplateRenderer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProfileHandler implements HttpHandler {
    private final TemplateRenderer renderer;
    private final Map<String, Employee> users;
    private final Map<UUID, String> activeSessions;
    private final LibraryData libraryData;

    public ProfileHandler(TemplateRenderer renderer, Map<String, Employee> users, Map<UUID, String> activeSessions, LibraryData libraryData) {
        this.renderer = renderer;
        this.users = users;
        this.activeSessions = activeSessions;
        this.libraryData = libraryData;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGet(exchange);
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Map<String, Object> dataModel = new HashMap<>();
        String email = CookieManager.authenticate(exchange, activeSessions);

        List<Book> userBooks;
        synchronized (libraryData) {
            String finalEmail = email;
            userBooks = libraryData.getBooks().stream()
                    .filter(book -> finalEmail.equals(book.getIssuedToEmployeeId()))
                    .collect(Collectors.toList());
        }

        dataModel.put("userBooks", userBooks);

        List<String> cookies = exchange.getRequestHeaders().get("Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("sessionId=")) {
                    String sessionIdStr = cookie.substring("sessionId=".length());
                    try {
                        UUID sessionId = UUID.fromString(sessionIdStr);
                        email = activeSessions.get(sessionId);
                        break;
                    } catch (IllegalArgumentException e) {
                    }
                }
            }
        }

        Employee user = users.get(email);

        if (user == null) {
            dataModel.put("userEmail", "ushallnot@pass.com");
            dataModel.put("userName", "**Некий пользователь**");
        } else {
            dataModel.put("userEmail", user.getEmail());
            dataModel.put("userName", user.getName());
        }

        try {
            String responseHTML = renderer.render("profile.ftlh", dataModel);
            ResponseSender.sendResponse(exchange, 200, responseHTML, "text/html; charset=UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
        }
    }
}