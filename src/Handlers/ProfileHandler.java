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
import java.util.Map;


public class ProfileHandler implements HttpHandler {
    private final TemplateRenderer renderer;
    private final Map<String, Employee> users;

    public ProfileHandler(TemplateRenderer renderer, Map<String, Employee> users) {
        this.renderer = renderer;
        this.users = users;
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
        String query = exchange.getRequestURI().getQuery();
        String email = null;

        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] entry = pair.split("=");
                if (entry.length == 2 && "email".equals(entry[0])) {
                    email = java.net.URLDecoder.decode(entry[1], StandardCharsets.UTF_8);
                    break;
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