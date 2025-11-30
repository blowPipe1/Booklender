package utils;

import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CookieManager {
    public static UUID getSessionIdFromCookie(HttpExchange exchange) {
        List<String> cookies = exchange.getRequestHeaders().get("Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("sessionId=")) {
                    String sessionIdStr = cookie.substring("sessionId=".length());
                    try {
                        return UUID.fromString(sessionIdStr);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public static void setSessionCookie(HttpExchange exchange, String sessionId, int maxAgeSeconds) {
        String cookieValue = String.format("sessionId=%s; Max-Age=%d; HttpOnly; Path=/", sessionId, maxAgeSeconds);
        exchange.getResponseHeaders().set("Set-Cookie", cookieValue);
    }

    public static String authenticate(HttpExchange exchange, Map<UUID, String> activeSessions) {
        UUID sessionId = getSessionIdFromCookie(exchange);
        if (sessionId != null) {
            return activeSessions.get(sessionId);
        }
        return null;
    }
}