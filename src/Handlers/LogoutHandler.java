package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.CookieManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

public class LogoutHandler implements HttpHandler {
    private final Map<UUID, String> activeSessions;

    public LogoutHandler(Map<UUID, String> activeSessions) {
        this.activeSessions = activeSessions;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        UUID sessionId = CookieManager.getSessionIdFromCookie(exchange);
        if (sessionId != null) {
            activeSessions.remove(sessionId);
            String expiredCookie = "sessionId=; Max-Age=0; HttpOnly; Path=/";
            exchange.getResponseHeaders().set("Set-Cookie", expiredCookie);
        }

        exchange.getResponseHeaders().set("Location", "/login?logout=1");
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_SEE_OTHER, -1);
    }
}