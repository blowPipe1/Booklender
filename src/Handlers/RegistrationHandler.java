package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import models.Employee;
import utils.TemplateRenderer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RegistrationHandler implements HttpHandler {
    private final TemplateRenderer renderer;
    private final Map<String, Employee> users;

    public RegistrationHandler(TemplateRenderer renderer, Map<String, Employee> users) {
        this.renderer = renderer;
        this.users = users;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGet(exchange, null);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            handlePost(exchange);
        } else {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1);
        }
    }

    private void handleGet(HttpExchange exchange, String message) throws IOException {
        Map<String, Object> dataModel = new HashMap<>();
        if (message != null) {
            dataModel.put("message", message);
        }
        try {
            String responseHTML = renderer.render("register.ftlh", dataModel);
            byte[] responseBytes = responseHTML.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        Map<String, String> formData = parseFormData(requestBody);
        String email = formData.get("email");
        String name = formData.get("name");
        String password = formData.get("password");

        if (email == null || name == null || password == null || users.containsKey(email)) {
            handleGet(exchange, "Регистрация не удалась: пользователь уже существует");
            return;
        }
        if (password == null) {
            handleGet(exchange, "ошибка при обработке пароля");
            return;
        }
        Employee newUser = new Employee(email, name, password);
        users.put(email, newUser);
        handleGet(exchange, "удачная регистрация! теперь вы можете войти");
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