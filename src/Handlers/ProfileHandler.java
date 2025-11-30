package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Employee;
import utils.TemplateRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfileHandler implements HttpHandler {
    private final TemplateRenderer renderer;
    private final Map<String, Employee> users;
    private final Map<UUID, String> activeSessions;

    public ProfileHandler(TemplateRenderer renderer, Map<String, Employee> users, Map<UUID, String> activeSessions) {
        this.renderer = renderer;
        this.users = users;
        this.activeSessions = activeSessions;
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
        String email = null;

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
            byte[] responseBytes = responseHTML.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
        }
    }
}