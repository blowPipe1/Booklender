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
            ResponseSender.sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        String email = null;

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("email=")) {
                    email = param.substring(6);
                    break;
                }
            }
        }


        String finalEmail = email;
        Employee employee = libraryData.getEmployees().stream()
                .filter(e -> e.getEmail().equals(finalEmail))
                .findFirst()
                .orElse(null);

        if (employee != null) {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("employee", employee);

            String finalEmail1 = email;
            List<Book> issuedBooks = libraryData.getBooks().stream()
                    .filter(b -> b.getIssuedToEmployeeId() != null && b.getIssuedToEmployeeId().equals(finalEmail1))
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
            ResponseSender.sendResponse(exchange, 404, "Employee not found");
        }
    }

}