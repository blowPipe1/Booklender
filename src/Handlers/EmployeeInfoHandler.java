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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeInfoHandler implements HttpHandler {
    private final LibraryData libraryData;
    private final TemplateRenderer renderer;

    public EmployeeInfoHandler(LibraryData libraryData, TemplateRenderer renderer) {
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
        String id = null;
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("id=")) {
                    id = param.substring(3);
                    break;
                }
            }
        }

        String finalId = id;
        Employee employee = libraryData.getEmployees().stream()
                .filter(e -> e.getId().equals(finalId))
                .findFirst()
                .orElse(null);

        if (employee != null) {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("employee", employee);

            String finalId1 = id;
            List<Book> issuedBooks = libraryData.getBooks().stream()
                    .filter(b -> b.getIssuedToEmployeeId() != null && b.getIssuedToEmployeeId().equals(finalId1))
                    .collect(Collectors.toList());

            dataModel.put("issuedBooks", issuedBooks);

            String response = "";
            try {
                response = renderer.render("employee-info.ftl", dataModel);
                sendResponse(exchange, 200, response, "text/html; charset=UTF-8");
            } catch (TemplateException e) {
                sendResponse(exchange, 500, "Template error: " + e.getMessage());
            }
        } else {
            sendResponse(exchange, 404, "Employee not found");
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