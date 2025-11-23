package server;

import Handlers.BookListHandler;
import com.sun.net.httpserver.HttpServer;
import models.LibraryData;
import utils.DataLoader;
import utils.TemplateRenderer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public Server(int port)throws IOException {
        LibraryData libraryData;
        try {
            libraryData = DataLoader.loadData();
        } catch (IOException e) {
            System.err.println("Error loading library data: " + e.getMessage());
            return;
        }

        TemplateRenderer renderer = new TemplateRenderer();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/books", new BookListHandler(libraryData, renderer));
//        server.createContext("/book-info", new BookInfoHandler(libraryData, renderer));
//        server.createContext("/employee-info", new EmployeeInfoHandler(libraryData, renderer));

        server.setExecutor(null);
        server.start();
        System.out.printf("Server started on port %s%n", port);
    }
}

