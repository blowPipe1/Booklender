import com.sun.net.httpserver.HttpServer;
import server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        try {
            Server server = new Server(9889);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}