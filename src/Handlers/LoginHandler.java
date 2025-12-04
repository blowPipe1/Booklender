package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Employee;
import utils.CookieManager;
import utils.DataParser;
import utils.ResponseSender;
import utils.TemplateRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class LoginHandler implements HttpHandler {
    private final TemplateRenderer renderer;
    private final Map<String, Employee> users;
    private final Map<UUID, String> activeSessions;

    public LoginHandler(TemplateRenderer renderer, Map<String, Employee> users, Map<UUID, String> activeSessions) {
        this.renderer = renderer;
        this.users = users;
        this.activeSessions = activeSessions;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGet(exchange);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            handlePost(exchange);
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Map<String, Object> dataModel = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("error=1")) {
            dataModel.put("errorMessage", "Авторизоваться не удалось: неверный идентификатор или пароль.");
        }

        try {
            String responseHTML = renderer.render("login.ftlh", dataModel);
            ResponseSender.sendResponse(exchange, 200, responseHTML, "text/html; charset=UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        Map<String, String> formData = DataParser.parseFormData(requestBody);
        String email = formData.get("email");
        String password = formData.get("password");

        Employee user = users.get(email);

        if (user == null || !verifyPassword(password, user.getPasswordHash())) {
            exchange.getResponseHeaders().set("Location", "/login?error=1");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
            return;
        }

        UUID sessionId = UUID.randomUUID();
        activeSessions.put(sessionId, email);
        CookieManager.setSessionCookie(exchange, sessionId.toString(), 600);

        exchange.getResponseHeaders().set("Location", "/profile");
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
    }

    private boolean verifyPassword(String rawPassword, String storedHash) {
        return rawPassword != null && rawPassword.equals(storedHash);
    }
}
