package server;

import Handlers.*;
import com.sun.net.httpserver.HttpServer;
import models.Employee;
import models.LibraryData;
import utils.DataLoader;
import utils.TemplateRenderer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final Map<String, Employee> users = new HashMap<>();

    public Server(int port)throws IOException {
        LibraryData libraryData;
        try {
            libraryData = DataLoader.loadData();
        } catch (IOException e) {
            System.err.println("Error loading library data: " + e.getMessage());
            return;
        }

        libraryData.getEmployees().forEach(emp -> users.put(emp.getEmail(), emp));

        TemplateRenderer renderer = new TemplateRenderer();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/books", new BookListHandler(libraryData, renderer));
        server.createContext("/book-info", new BookInfoHandler(libraryData, renderer));
        server.createContext("/employee-info", new EmployeeInfoHandler(libraryData, renderer));  // http://localhost:9889/employee-info?email=petr.petrov@mail.com для проверки

        server.createContext("/register", new RegistrationHandler(renderer, users));

        server.setExecutor(null);
        server.start();
        System.out.printf("Server started on port %s%n", port);
    }
}

