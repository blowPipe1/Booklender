package Handlers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
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

public class EmployeeInfoHandler implements HttpHandler {
    private final LibraryData libraryData;
    private final TemplateRenderer renderer;
    private final Map<UUID, String> activeSessions;

    public EmployeeInfoHandler(LibraryData libraryData, TemplateRenderer renderer, Map<UUID, String> activeSessions) {
        this.libraryData = libraryData;
        this.renderer = renderer;
        this.activeSessions = activeSessions;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            ResponseSender.sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        UUID sessionId = CookieManager.getSessionIdFromCookie(exchange);
        String sessionEmail = (sessionId != null) ? activeSessions.get(sessionId) : null;

        if (sessionEmail == null) {
            exchange.getResponseHeaders().set("Location", "/login");
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
            exchange.close();
            return;
        }

        String employeeEmail = sessionEmail;

        Employee employee = libraryData.getEmployees().stream()
                .filter(e -> e.getEmail().equals(employeeEmail))
                .findFirst()
                .orElse(null);

        if (employee != null) {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("employee", employee);

            List<Book> issuedBooks = libraryData.getBooks().stream()
                    .filter(b -> b.getIssuedToEmployeeId() != null && b.getIssuedToEmployeeId().equals(employeeEmail))
                    .collect(Collectors.toList());

            dataModel.put("issuedBooks", issuedBooks);

            String response = "";
            try {
                response = renderer.render("employee-info.ftlh", dataModel);
                ResponseSender.sendResponse(exchange, 200, response, "text/html; charset=UTF-8");
            } catch (TemplateException e) {
                ResponseSender.sendResponse(exchange, 500, "Template error: " + e.getMessage());
            }
        } else {
            ResponseSender.sendResponse(exchange, 404, "Employee data not found for authenticated user.");
        }
    }
}