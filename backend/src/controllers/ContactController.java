package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.ContactService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ContactController implements HttpHandler {
    private final ContactService contactService;
    private final Gson gson;

    public ContactController() {
        contactService = new ContactService();
        gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case "/contacts/add":
                if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    addContact(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }
                break;

            case "/contacts/user":
                if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                    getContacts(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }
                break;

            case "/contacts/delete":
                if (exchange.getRequestMethod().equalsIgnoreCase("DELETE")) {
                    deleteContact(exchange);
                } else {
                    sendResponse(exchange, "Method not allowed", 405);
                }
                break;

            default:
                sendResponse(exchange, "Route not found", 404);
        }
    }

    private void addContact(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();
        String contactUserId = json.get("contactUserId").getAsString();

        String result = contactService.addContact(ownerId, contactUserId);

        if (result.equals("SUCCESS")) {
            sendResponse(exchange, result, 200);
        } else {
            sendResponse(exchange, result, 400);
        }
    }

    private void getContacts(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("userId=")) {
            sendResponse(exchange, "Missing userId", 400);
            return;
        }

        String userId = query.substring("userId=".length());
        sendResponse(exchange, contactService.getContacts(userId), 200);
    }

    private void deleteContact(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        JsonObject json = gson.fromJson(reader, JsonObject.class);

        String ownerId = json.get("ownerId").getAsString();
        String contactUserId = json.get("contactUserId").getAsString();

        String result = contactService.removeContact(ownerId, contactUserId);

        if (result.equals("SUCCESS")) {
            sendResponse(exchange, result, 200);
        } else {
            sendResponse(exchange, result, 400);
        }
    }

    private void sendResponse(HttpExchange exchange, Object response, int statusCode) throws IOException {
        String json = gson.toJson(response);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

        byte[] bytes = json.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }
}
